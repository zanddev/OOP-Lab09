package it.unibo.oop.lab.lambda.ex03;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;

//import java.util.Arrays;
//import java.util.Collections;
import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Modify this small program adding new filters.
 * Realize this exercise using as much as possible the Stream library.
 * 
 * 1) Convert to lowercase
 * 
 * 2) Count the number of chars
 * 
 * 3) Count the number of lines
 * 
 * 4) List all the words in alphabetical order
 * 
 * 5) Write the count for each word, e.g. "word word pippo" should output "pippo -> 1 word -> 2"
 *
 */
public final class LambdaFilter extends JFrame {

    private static final long serialVersionUID = 1760990730218643730L;

    private enum Command {
        //IDENTITY("No modifications", x -> x),
        IDENTITY("No modifications", Function.identity()),

        //LOWERCASE("Lowercase", x -> x.toLowerCase()),
        LOWERCASE("Lowercase", x -> x.lines()
                .map(elem -> elem.toLowerCase())
                .reduce("", (acc, elem) -> acc.concat(elem + "\n"))),

        //CHARS("Number of chars", x -> String.valueOf(x.length())),
        CHARS("Number of chars", x -> String.valueOf(Stream.generate(() -> x).mapToInt(e -> e.length()).sum())),
        //CHARS("Number of chars", x -> String.valueOf(Stream.of(x).mapToInt(e -> e.length()).sum())),

        //LINES("Number of lines", x -> String.valueOf(x.split("\n").length)),
        LINES("Number of lines", x -> String.valueOf(x.lines().count())),

        /*SORT("Alphabetical order", x -> {
            //final List<String> words = Arrays.asList(x.split("\n"));

            //final List<String> words = new LinkedList<>();
            //x.lines().forEach(elem -> words.add(elem));

            final List<String> words = x.lines().collect(Collectors.toList());
            Collections.sort(words);
            //words.forEach(e -> e = e + "\n");
            return String.join("\n", words);
        }),*/
        SORT("Alphabetical order", x -> x.lines().sorted().reduce("", (acc, elem) -> (acc.concat(elem + "\n")))),

    /*
        COUNT("Count the words", x -> {
            final Map<String, Integer> words = new HashMap<>();
            x.lines().forEach(word -> {
                if (!word.trim().equals("")) {
                    if (words.containsKey(word)) {
                        words.put(word, words.get(word) + 1);
                    } else {
                        words.put(word, 1);
                    }
                }
                return;
            });
            final List<String> list = new LinkedList<>();
            words.entrySet().forEach(entry -> list.add(entry.getKey() + " -> " + entry.getValue()));
            return String.join("\n", list.toArray(new String[0]));
        });
    */
        COUNT("Count the words", x -> {
            final Map<String, Integer> words = new HashMap<>();
            x.lines().forEach(word -> {
                if (!word.trim().equals("")) {
                    if (words.containsKey(word)) {
                        words.put(word, words.get(word) + 1);
                    } else {
                        words.put(word, 1);
                    }
                }
                return;
            });
            return words.entrySet()
                    .stream()
                    .map(entry -> (entry.getKey() + " -> " + entry.getValue()))
                    .collect(Collectors.joining(" "));
        });

        private final String commandName;
        private final Function<String, String> fun;

        Command(final String name, final Function<String, String> process) {
            commandName = name;
            fun = process;
        }

        @Override
        public String toString() {
            return commandName;
        }

        public String translate(final String s) {
            return fun.apply(s);
        }
    }

    private LambdaFilter() {
        super("Lambda filter GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JPanel panel1 = new JPanel();
        final LayoutManager layout = new BorderLayout();
        panel1.setLayout(layout);
        final JComboBox<Command> combo = new JComboBox<>(Command.values());
        panel1.add(combo, BorderLayout.NORTH);
        final JPanel centralPanel = new JPanel(new GridLayout(1, 2));
        final JTextArea left = new JTextArea();
        left.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        final JTextArea right = new JTextArea();
        right.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        right.setEditable(false);
        centralPanel.add(left);
        centralPanel.add(right);
        panel1.add(centralPanel, BorderLayout.CENTER);
        final JButton apply = new JButton("Apply");
        apply.addActionListener(ev -> right.setText(((Command) combo.getSelectedItem()).translate(left.getText())));
        panel1.add(apply, BorderLayout.SOUTH);
        setContentPane(panel1);
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int sw = (int) screen.getWidth();
        final int sh = (int) screen.getHeight();
        setSize(sw / 4, sh / 4);
        setLocationByPlatform(true);
    }

    /**
     * @param a unused
     */
    public static void main(final String... a) {
        final LambdaFilter gui = new LambdaFilter();
        gui.setVisible(true);
    }
}
