package fileio;

import graph.GraphEdge;
import graph.GraphNode;
import graph.OutputGraphNode;

import java.io.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Write {
    private Map<String, GraphNode> _algoResultMap;
    private BufferedReader _bufferedReader;
    private static final String RGX_NODE = "\t([a-zA-Z0-9]+)\t \\[Weight=([0-9]+)];";
    private static final String RGX_UNTIL_WEIGHT = "(.*)];";
    private String _outputPath;

    /**
     * @param algoResultMap
     */
    void writeFile(Map<String, GraphNode> algoResultMap, String outputPath) {
        _algoResultMap = algoResultMap;
        _outputPath = outputPath;
        try {
            _bufferedReader = new BufferedReader(new FileReader(outputPath));
            buildFile();
        } catch (IOException e) {
            System.out.println("IO exception");
            System.exit(1);
        }
    }

    void buildFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(_outputPath));

        // read
        try {
            String currentLine = _bufferedReader.readLine();
            String nextLine = _bufferedReader.readLine();

            while (currentLine!= null) {
                if (lineIsNode(currentLine)) {
                    // get node
                    Pattern patternNode = Pattern.compile(RGX_NODE);
                    Matcher matcherNode = patternNode.matcher(currentLine);
                    String node = matcherNode.group(0);
                    GraphNode graphNode = _algoResultMap.get(node);

                    Pattern p = Pattern.compile(RGX_UNTIL_WEIGHT);
                    Matcher m = patternNode.matcher(currentLine);

                    String appendToLine = ",Start=" + graphNode.getStartTime() + ",Processor=" + graphNode.getProcessor() + "];";
                    String newLine = new StringBuilder().append(m.group(0)).append(appendToLine).toString();
                    System.out.println(newLine);
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

        writer.close();
    }

    /**
     * @return true if line being read is node, false if not
     */
    boolean lineIsNode(String line) {
        Pattern patternNode = Pattern.compile(RGX_NODE);
        Matcher matcherNode = patternNode.matcher(line);

        if (matcherNode.find()) {
            return true;
        } else {
            return false;
        }
    }







}
