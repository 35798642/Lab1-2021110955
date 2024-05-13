package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;
import java.util.*;


public class UI extends JFrame {
    private Map<String, Map<String, Integer>> graph;
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private JTextField word1Field;
    private JTextField word2Field;
    private JTextField newTextField;

    private  boolean flag = true;
    public UI() {
        graph = new HashMap<>();
        textArea = new JTextArea();
        textArea.setEditable(false);
        fileChooser = new JFileChooser();

        JButton loadButton = new JButton("Load Text File");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(UI.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    processTextFile(file.getAbsolutePath());
                    GraphVisualizer.showDirectedGraph(graph);

                }
            }
        });

        word1Field = new JTextField(10);
        word2Field = new JTextField(10);
        JButton queryButton = new JButton("Query Bridge Words");
        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word1 = word1Field.getText();
                String word2 = word2Field.getText();
                String result = queryBridgeWords(word1, word2);
                System.out.println(result);
                textArea.append(result + "\n");
            }
        });

        newTextField = new JTextField(20);
        JButton generateButton = new JButton("Generate New Text");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputText = newTextField.getText();
                // TODO: Implement generateNewText method
                String result = generateNewText(inputText);
                textArea.append(result + "\n");
            }
        });

        JButton shortestPathButton = new JButton("Calculate Shortest Path");
        shortestPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word1 = word1Field.getText();
                String word2 = word2Field.getText();
                if(word1.isEmpty() && !word2.isEmpty()){
                        flag=true;
                        // 遍历图中的所有节点，计算源节点到所有节点的最短路径
                        for (String node : graph.keySet()) {
                            if (!node.equals(word2)) {
                                String shortestPath = calcShortestPath(node,word2);
                                textArea.append(shortestPath + "\n");
                            }
                        }
                }
                else if(word2.isEmpty() && !word1.isEmpty()){
                        flag=false;
                        // 遍历图中的所有节点，计算源节点到所有节点的最短路径
                        for (String node : graph.keySet()) {
                            if (!node.equals(word1)) {
                                String shortestPath = calcShortestPath(word1,node);
                                textArea.append(shortestPath + "\n");
                            }
                        }
                }
                else if(word2!=null && word1 != null) {// TODO: Implement calcShortestPath method
                    String result = calcShortestPath(word1, word2);
                    textArea.append(result + "\n");
                }
                else
                {
                    textArea.append("please input at least one node name!" + "\n");
                }
            }
        });

        JButton randomWalkButton = new JButton("Random Walk");
        randomWalkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String result = randomWalk();
                textArea.append(result + "\n");
            }
        });


        // 创建一个GridBagLayout布局的JPanel
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 设置按钮在GridBagLayout中的位置和大小
        gbc.fill = GridBagConstraints.BOTH; // 设置组件填充方式为填充整个单元格
        gbc.weightx = 1; // 设置组件水平拉伸权重
        gbc.weighty = 1; // 设置组件垂直拉伸权重
        gbc.ipadx = 10; // 设置组件内部填充的水平大小
        gbc.ipady = 10; // 设置组件内部填充的垂直大小

        // 将按钮添加到buttonPanel中
        gbc.gridx = 0; // 设置组件的起始列
        gbc.gridy = 0; // 设置组件的起始行
        gbc.gridx = 2;
        buttonPanel.add(loadButton, gbc);
        gbc.gridx++;
        buttonPanel.add(randomWalkButton, gbc);
        gbc.gridy++;
        gbc.gridx = 0; // 将列索引增加1，以便按钮在同一行
        buttonPanel.add(word1Field, gbc);
        gbc.gridx++;
        buttonPanel.add(word2Field, gbc);
        gbc.gridx++;
        buttonPanel.add(queryButton, gbc);
        gbc.gridx++;
        buttonPanel.add(shortestPathButton, gbc);
        gbc.gridy++; // 将行索引增加1，以便按钮在新行开始
        gbc.gridx = 0;
        buttonPanel.add(newTextField, gbc);
        gbc.gridx = 2; // 将行索引增加1，以便按钮在新行开始
        buttonPanel.add(generateButton, gbc);

        setLayout(new BorderLayout());
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        // 将buttonPanel添加到BorderLayout的CENTER位置
        add(buttonPanel, BorderLayout.SOUTH);

        setTitle("Text Graph Processor");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ... 其他方法，如processTextFile, showDirectedGraph, queryBridgeWords等


    // 以下是一些占位方法，用于演示按钮的功能
    private String generateNewText(String inputText) {
        String[] words = inputText.toLowerCase().split("\\s+");
        StringBuilder newText = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];

            // Check if word1 and word2 are in the graph
            if (graph.containsKey(word1) && graph.containsKey(word2)) {
                // Find all bridge words from word1 to word2
                List<String> bridgeWords = new ArrayList<>();
                for (String bridgeWord : graph.get(word1).keySet()) {
                    if (graph.get(bridgeWord).containsKey(word2)) {
                        bridgeWords.add(bridgeWord);
                    }
                }
                newText.append(word1).append(" ");
                // If there is a bridge word, insert it into the new text
                if (!bridgeWords.isEmpty()) {
                    int randomIndex = random.nextInt(bridgeWords.size());
                    newText.append(bridgeWords.get(randomIndex)).append(" ");
                }
            } else {
                newText.append(word1).append(" ");
            }
        }

        newText.append(words[words.length - 1]); // Append the last word

        return newText.toString();
    }


    private String calcShortestPath(String word1, String word2) {
        if (graph.containsKey(word1) && graph.containsKey(word2)) {
            List<String> paths = calshortestpaths.calcShortestPath(graph, word1, word2);
            System.out.println(paths);
            List<List<String>> wordLists = new ArrayList<>();

            for (String input : paths) {
                // 使用箭头"->"作为分隔符分割字符串
                String[] words = input.split("->");
                // 将分割后的单词数组转换为列表
                List<String> wordList = new ArrayList<>(Arrays.asList(words));
                wordLists.add(wordList);
            }
            String res=null;
            Integer i=1;
            String tmp;
            if(flag)  tmp=word1;
            else tmp=word2;

            for (List<String> singlepath : wordLists) {
                GraphVisualizer.showshortest(singlepath,i,tmp);
                String shortestPathLength = String.valueOf(singlepath.size()-1);
                res=res+"The shortest path from \"" + word1 + "\" to \"" + word2 + "\" is: " + String.join(" -> ", singlepath) + "\n"+ "The shortest path's length is "+shortestPathLength+"\n";
                i++;
            }
            if(res == null ) res="cannot reach!";
            return res;
        }
        else{
            String tip = "节点不存在！";
            return tip;
        }
    }


    private String randomWalk() {
        // Choose a random starting node
        List<String> nodes = new ArrayList<>(graph.keySet());
        Collections.shuffle(nodes);
        String currentNode = nodes.get(0);

        // Initialize the walk
        List<String> walk = new ArrayList<>();
        Set<String> visitedEdges = new HashSet<>();
        Random random = new Random();

        // Perform the random walk
        while (true) {
            walk.add(currentNode);

            // Get the outgoing edges from the current node
            Map<String, Integer> outgoingEdges = graph.get(currentNode);
            if (outgoingEdges.isEmpty()) {
                break; // No outgoing edges, end the walk
            }

            // Choose a random edge
            List<String> edges = new ArrayList<>(outgoingEdges.keySet());
            Collections.shuffle(edges);
            String nextNode = edges.get(0);

            // Check if the edge has been visited before
            String edge = currentNode + " -> " + nextNode;
            if (visitedEdges.contains(edge)) {
                walk.add(nextNode);
                break; // Repeated edge, end the walk
            }

            // Mark the edge as visited and move to the next node
            visitedEdges.add(edge);
            currentNode = nextNode;
        }

        // Write the walk to a file
        String walkText = String.join(" ", walk);
        try {
            writeWalkToFile(walkText);
        } catch (IOException e) {
            return "Error writing walk to file: " + e.getMessage();
        }

        return "Random walk result written to file.";
    }

    private void writeWalkToFile(String walkText) throws IOException {
        String fileName = "random_walk_result.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(walkText);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new UI();
            }
        });
    }

    public void processTextFile(String filePath) {
        graph.clear(); // 清空之前的图
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String previousWord = null;
            while ((line = reader.readLine()) != null) {
                String[] words = line.toLowerCase().split("\\s+");
                for (String word : words) {
                    word = word.replaceAll("[^a-z]", ""); // 移除非字母字符
                    System.out.println(word);
                    if (!word.isEmpty()) {
                        // 如果图中不存在该单词，则添加到图中
                        graph.putIfAbsent(word, new HashMap<>());
                        // 如果不是文本的第一个单词，则更新图
                        if (previousWord != null) {
                            graph.get(previousWord).put(word, graph.get(previousWord).getOrDefault(word, 0) + 1);
                        }
                        previousWord = word;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        printGraph();

    }

    public void printGraph() {
        for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
            String sourceWord = entry.getKey();
            Map<String, Integer> edges = entry.getValue();
            System.out.println("Source Word: " + sourceWord);
            for (Map.Entry<String, Integer> edge : edges.entrySet()) {
                String targetWord = edge.getKey();
                int weight = edge.getValue();
                System.out.println("  -> " + targetWord + " (" + weight + ")");
            }
        }
    }




    public String queryBridgeWords(String word1, String word2) {
        if(!graph.containsKey(word1) && graph.containsKey(word2)){
            return "No \""+ word1 + "\" in the graph!";
        }
        else if(!graph.containsKey(word2) && graph.containsKey(word1)){
            return "No \""+ word2 + "\" in the graph!";
        }
        else if(!graph.containsKey(word1) && !graph.containsKey(word2)){
            return "No \""+ word1 + "\" and \"" + word2 + "\" in the graph!";
        }


        List<String> bridgeWords = new ArrayList<>();
        for (String bridgeWord : graph.get(word1).keySet()) {
            if (graph.get(bridgeWord).containsKey(word2)) {
                bridgeWords.add(bridgeWord);
            }
        }

        if (bridgeWords.isEmpty()) {
            return "No bridge words from \""+ word1 + "\" to \"" + word2 + "\"!";
        } else {
            if(bridgeWords.size()==1){
                return "The bridge words from \""+ word1 + "\" to \"" + word2 + "\" is: " + String.join(", ", bridgeWords);
            }
            else{
                return "The bridge words from \""+ word1 + "\" to \"" + word2 + "\" are: " + String.join(", ", bridgeWords);
            }
        }
    }


}