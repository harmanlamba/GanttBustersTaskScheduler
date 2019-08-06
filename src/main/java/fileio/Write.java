package fileio;

import graph.GraphEdge;
import graph.GraphNode;
import graph.OutputGraphNode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Write {
    private Map<GraphNode, OutputGraphNode> _algoResultMap;
    private static final String RGX_NODE = "\t([a-zA-Z0-9]+)\t \\[Weight=([0-9]+)];";
    private String outputPath;

    /**
     * @param algoResultMap
     */
    void writeFile(Map<GraphNode, OutputGraphNode> algoResultMap, String outputPath) {
        _algoResultMap = algoResultMap;
        _outputPath = outputPath;
        buildFile();
    }

    void buildFile() {
        BufferedWriter writer = new BufferedWriter(new FileWriter(_outputPath));
//
//        writer.write(str);
//        writer.close();

        // read
        try {
            String currentLine = _bufferedReader.readLine();
            String nextLine = _bufferedReader.readLine();

            while (currentLine!= null) {
                if (lineIsNode()) {
                    String appendToLine = "";
                    String newLine = new StringBuilder().append(currentLine).append(appendToLine).toString();
                    writer.write(newLine);
                } else {
                    writer.write(currentLine);
                }
                currentLine = nextLine;
                nextLine = _bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return true if line being read is node, false if not
     */
    boolean lineIsNode() {
        Pattern patternNode = Pattern.compile(RGX_NODE);
        Matcher matcherNode = patternNode.matcher(line);

        if (matcherNode.find()) {
            return true;
        } else {
            return false;
        }
    }





}
