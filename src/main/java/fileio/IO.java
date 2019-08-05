package fileio;

import graph.GraphEdge;
import graph.GraphNode;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IO {
    private BufferedReader _bufferedReader;
    private List<GraphNode> _vertexList;
    private List<GraphEdge> _edgeList;
    private static final String RGX_NODE = "\t[a-z]+\t \\[Weight=[0-9]+\\];";
    //private static final String RGX_EDGE = "\\"

    public IO(String filePath) {
        _vertexList = new ArrayList<>();
        _edgeList = new ArrayList<>();

        try {
            _bufferedReader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void readFile() {
        try {
            //Need to take command line arguments so we take in FileReader(args.toString())
            String currentLine = _bufferedReader.readLine();
            String nextLine = _bufferedReader.readLine();
            boolean hasStarted = false;

            //TODO: Remove prints
            while (currentLine!= null) {
                if (!hasStarted) {
                    //Check if first line correct
                    System.out.println(currentLine);
                    hasStarted = true;
                } else if (nextLine == null) {
                    //Check last line correct
                    System.out.println(currentLine);
                } else {
                    //In between lines
                    lineRegex(currentLine, RGX_NODE);
                    System.out.println(currentLine);
                }

                currentLine = nextLine;
                nextLine = _bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String lineRegex(String line, String regex) {
        Pattern pattern = Pattern.compile(regex);
        String test = line.replaceAll(" ", "");
        Matcher matcher = pattern.matcher(test);
        if (matcher.find()) {
            System.out.println(matcher.group(1) + "Hello");
            return matcher.group(1);
        } else {
            System.out.println("nothign");
            return "";
        }
    }

}
