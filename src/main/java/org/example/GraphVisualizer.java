package org.example;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.*;
import guru.nidi.graphviz.model.Factory;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.Node;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphVisualizer {
    /*get new line*/static String newLine=System.getProperty("line.separator");    //获得当前系统的换行符
    static String osName=System.getProperty("os.name");

    public static void showDirectedGraph(Map<String, Map<String, Integer>> graph) {
        StringBuilder dotText = new StringBuilder();
        dotText.append(String.format("digraph G{"+newLine));

        Set<String> nodeNames = new HashSet<>();

        // 遍历外层 Map 来获取所有的节点名
        for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
            String nodeName = entry.getKey();
            nodeNames.add(nodeName);

            // 遍历内层 Map 来获取所有的相邻节点名
            Map<String, Integer> neighbors = entry.getValue();
            for (String neighborName : neighbors.keySet()) {
                nodeNames.add(neighborName);
            }
        }
        //所有节点
        for (String nodeName : nodeNames) {
            dotText.append(nodeName);
            dotText.append(";"+newLine);
        }
        //边
        for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
            String sourceWord = entry.getKey();
            Map<String, Integer> edges = entry.getValue();
            for (Map.Entry<String, Integer> edge : edges.entrySet()) {
                String targetWord = edge.getKey();
                int weight = edge.getValue();
                dotText.append(String.format("%s->%s[label=%d]", sourceWord,targetWord,weight));
                dotText.append(";"+newLine);
            }
        }
        dotText.append("}"+newLine);    //写入结束

        //把生成好的脚本写到指定的缓存路径下
        String graphFilePath="graph.gv";
        try {
            File tmpf=new File("/tmp");
            if(!tmpf.exists()) {
                tmpf.mkdirs();
            }
            FileWriter fw=new FileWriter(graphFilePath);
            BufferedWriter bufw = new BufferedWriter(fw);
            bufw.write(dotText.toString());
            bufw.close();
        }catch (Exception e) {
            throw new RuntimeException("Failed to open file");
        }
        saveandshow("graph.gv",0,"all");

    }

    private static void saveandshow(String dotFile,int label,String word) {
        String outputFile = null;
        if(label == 0){
            outputFile = "origin.png";
        }
        else{
            outputFile = "shortestpath" + word + label + ".png";
        }
        String graphvizPath ="D:\\Program Files\\windows_10_cmake_Release_Graphviz-11.0.0-win64\\Graphviz-11.0.0-win64\\bin\\dot.exe";
        try {
            // 调用Graphviz
            ProcessBuilder pb = new ProcessBuilder(graphvizPath, "-Tpng", dotFile, "-o", outputFile);
            Process process = pb.start();
            process.waitFor();

            System.out.println("Graphviz output generated: " + outputFile);

            // 读取图像文件
            BufferedImage image = ImageIO.read(new File(outputFile));

            // 显示图像
            JFrame frame = new JFrame(outputFile);
            frame.getContentPane().setLayout(new FlowLayout());
            frame.getContentPane().add(new JLabel(new ImageIcon(image)));
            frame.pack();
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // 保存图像到本地
            ImageIO.write(image, "PNG", new File("local_copy.png"));
            System.out.println("Image saved to local_copy.png");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }




    public  static void showshortest(List<String> path, int label, String word1) {
        String dotFilePath = "graph.gv";
//        List<String> path = Arrays.asList("new", "worlds", "to", "explore", "strange", "new"); // 假设这是要标红的路径
        String outputFile = "shortest_path"+word1+label+".gv";
        try {
            // 读取DOT文件
            BufferedReader reader = new BufferedReader(new FileReader(dotFilePath));
            StringBuilder dotContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                dotContent.append(line).append("\n");
            }
            reader.close();

            // 解析DOT文件并标记路径中的边为红色，起点和终点为黄色
            String modifiedDotContent = highlightPathInDot(dotContent.toString(), path);

            // 将修改后的DOT文件内容写入新的文件

            FileWriter writer = new FileWriter(outputFile);
            writer.write(modifiedDotContent);
            writer.close();

            System.out.println("Highlighted DOT file created: " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        saveandshow(outputFile,label,word1);
    }

    private static String highlightPathInDot(String dotContent, List<String> path) {
        StringBuilder modifiedDotContent = new StringBuilder();
        Set<String> pathSet = new HashSet<>(path); // 用于快速查找

        // 分割DOT文件内容，并标记路径中的边为红色，起点和终点为黄色
        for (String line : dotContent.split("\n")) {
            // 检查当前边是否在路径中
            for (int i = 0; i < path.size() - 1; i++) {
                String node1 = path.get(i);
                String node2 = path.get(i + 1);
                // 检查当前行是否定义了一个边，并且边的起点和终点都在路径中

                if (line.trim().matches(node1 + "->" + node2 + "\\[.*\\];")) {
                    // 标记为红色
//                    line = line.replaceFirst("(\\[.*\\])", "[color=blue, $1");
                    // 定义正则表达式
                    String regex = "\\[label=(\\d+)\\]";

                    // 创建 Pattern 对象
                    Pattern pattern = Pattern.compile(regex);

                    // 创建 Matcher 对象
                    Matcher matcher = pattern.matcher(line);

                    // 使用 Matcher 对象进行匹配和替换
                    line = matcher.replaceAll("[label=$1, color=blue]");
                    break;
                }

            }

            // 检查当前节点是否是路径的起点或终点
            for (String node : pathSet) {

                    // 确保node以;结尾
                    String nodeWithSemicolon = node + ";";
                    if (line.trim().equals(nodeWithSemicolon)) {
                        // 如果是起点或终点，则标记为黄色
                        if (line.contains("[")) {
                            // 如果有属性列表，则在现有的属性列表后面追加color=yellow
                            line = line.replaceFirst("(\\[.*\\])", "$1, color=yellow");
                        } else {
                            // 如果没有属性列表，则在末尾添加属性列表并设置color=yellow
                            line = line.replaceFirst(";", " [color=red];");
                        }
                    }
            }

            modifiedDotContent.append(line).append("\n");
        }

        return modifiedDotContent.toString();
    }


}










