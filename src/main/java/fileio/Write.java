package fileio;

import graph.GraphNode;

import java.io.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Write {
    private Map<String, GraphNode> _algorithmResultMap;
    private BufferedReader _bufferedReader;
    private static final String RGX_NODE = "\t([a-zA-Z0-9]+)\t \\[Weight=([0-9]+)];";
    private static final String RGX_OUTPUT_FILENAME = ".*?/ .dot";
    //private static final String RGX_UNTIL_WEIGHT = "([.*])];";
    private String _outputPath;
    private String _inputPath;
    private File _outputFile;

    public Write(String inputPath, String outputPath) {
        _outputPath = outputPath;
        _inputPath = inputPath;
    }

    /**
     * @param algorithmResultMap
     */
    public void writeToPath(Map<String, GraphNode> algorithmResultMap) {
        _algorithmResultMap = algorithmResultMap;
        try {
            _outputFile = new File(_outputPath);
            _outputFile.createNewFile();
            _bufferedReader = new BufferedReader(new FileReader(_inputPath));
            buildFile();
        } catch (IOException e) {
            System.out.println("IO exception");
            System.exit(1);
        }
    }

    private void buildFile() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(_outputPath));

        // read
        try {
            String currentLine = _bufferedReader.readLine();
            String nextLine = _bufferedReader.readLine();
            boolean firstLine = true;

            while (currentLine!= null) {
                if (lineIsNode(currentLine) != null) {
                    // get node
                    GraphNode graphNode = _algorithmResultMap.get(lineIsNode(currentLine));

                    String appendToLine = ",Start=" + graphNode.getStartTime() + ",Processor=" + graphNode.getProcessor() + "];\n";
                    StringBuilder sb =  new StringBuilder();
                    sb.append(currentLine);
                    sb.setLength(sb.length()-2);
                    sb.append(appendToLine);
                    writer.write(sb.toString());

                } else if (firstLine) {
                    StringBuilder sb =  new StringBuilder();
                    sb.append("digraph \"");
                    sb.append(_outputFile.getName());
                    sb.setLength(sb.length() - 4);
                    sb.append("\" {\n");
                    writer.write(sb.toString());
                    firstLine = false;

                } else {
                    writer.write(currentLine.concat("\n"));
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
    private String lineIsNode(String line) {
        Pattern patternNode = Pattern.compile(RGX_NODE);
        Matcher matcherNode = patternNode.matcher(line);

        if (matcherNode.find()) {
            return matcherNode.group(1);
        } else {
            return null;
        }
    }
}
