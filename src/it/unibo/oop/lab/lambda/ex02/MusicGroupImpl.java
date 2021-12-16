package it.unibo.oop.lab.lambda.ex02;

//import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
//import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        //return this.songs.stream().map(song -> song.getSongName()).sorted();
        return this.songs.stream().map(Song::getSongName).sorted();
    }

    @Override
    public Stream<String> albumNames() {
        //return this.albums.entrySet().stream().map(entry -> entry.getKey());
        return this.albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        //Set<Entry<String, Integer>> x = this.albums.entrySet();
        //x.removeIf(entry -> entry.getValue() != year);
        //return x.stream().map(entry -> entry.getKey());
        return this.albums.entrySet().stream().filter(entry -> entry.getValue() == year).map(entry -> entry.getKey());
    }

    @Override
    public int countSongs(final String albumName) {
        //return this.songs.stream()
        //        .filter(song -> song.getAlbumName().isPresent())
        //        .filter(song -> song.getAlbumName().get().equals(albumName)).toArray().length;
        return (int) this.songs.stream()
                .filter(song -> song.getAlbumName().isPresent())
                .filter(song -> song.getAlbumName().get().equals(albumName))
                .count();
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) this.songs.stream()
                .filter(song -> song.getAlbumName().isEmpty())
                .count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
    /*
        return OptionalDouble.of(this.songs.stream()
                .filter(song -> song.getAlbumName().isPresent())
                .filter(song -> song.getAlbumName().get().equals(albumName))
                .map(song -> song.getDuration())
                .collect(Collectors.averagingDouble(x -> x))
                );
    */
        return this.songs.stream()
               .filter(song -> song.getAlbumName().isPresent())
               .filter(song -> song.getAlbumName().get().equals(albumName))
               .mapToDouble(song -> song.getDuration())
               .average();
    }

    @Override
    public Optional<String> longestSong() {
        OptionalDouble max = this.songs.stream()
                .filter(song -> song.getAlbumName().isPresent())
                .mapToDouble(song -> song.getDuration()).max();
        //System.out.println(max.toString());

        Optional<String> longestSong = Optional.empty();
        for (Song song : songs) {
            if (Optional.of(song.getDuration()).get().compareTo(max.getAsDouble()) == 0) {
                longestSong = Optional.of(song.getSongName());
            }
        }
        //System.out.println(longestSong);
        return longestSong;
    /*
        // Sol
        return this.songs.stream()
                .collect(Collectors.maxBy(Comparator.comparingDouble(Song::getDuration)))
                //.collect(Collectors.maxBy((a, b) -> Double.compare(a.getDuration(), b.getDuration()))
                .map(Song::getSongName);
    */
    }

    @Override
    public Optional<String> longestAlbum() {
        final Map<String, OptionalDouble> m = new HashMap<>();

        this.albums.forEach((key, value) -> m.put(key, OptionalDouble.empty()));
        m.forEach((key, value) -> {
            m.put(key, OptionalDouble.of(
                this.songs.stream()
                    .filter(song -> song.getAlbumName().isPresent())
                    .filter(song -> song.getAlbumName().get().equals(key))
                    .mapToDouble(song -> song.getDuration()).sum()));
        });
        //System.out.println(m.toString());

        Optional<Double> max = m.entrySet().stream().map(entry -> entry.getValue().orElseThrow()).max((Double::compare));
        //System.out.println(max.toString());

        Optional<String> longestAlbum = Optional.empty();
        for (Entry<String, OptionalDouble> entry : m.entrySet()) {
            if (Optional.of(entry.getValue().getAsDouble()).get().compareTo(max.get()) == 0) {
                longestAlbum = Optional.of(entry.getKey());
            }
        }
        //System.out.println(longestAlbum);
        return longestAlbum;
/*
        // Sol
        return this.songs.stream().filter(a -> a.getAlbumName().isPresent())
                .collect(Collectors.groupingBy(Song::getAlbumName, Collectors.summingDouble(Song::getDuration)))
                .entrySet().stream()
                .collect(Collectors.maxBy(Comparator.comparingDouble(Entry::getValue)))
                .flatMap(Entry::getKey);
*/
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
