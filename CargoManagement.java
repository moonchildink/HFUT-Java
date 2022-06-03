import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//第一次调试：Error.txt为空，shipping.txt为空
//newInventory.txt 之中根本没有进行删改
public class CargoManagement {
    public static void main(String[] args) throws Exception {
        new getInventory();
        System.out.println("文件处理完成。");
    }
}

// 库存货物类
class Goods {
    // Item number 字符串型，货物编号
    // Quantity 整型，货物数量
    // Supplier 字符串型，供应商编号
    // Description 字符串型，货物描述

    String itemNumber;
    int quantity;
    String supplier;
    String description;

    public Goods(String itemNumber, String quantity, String supplier, String description) {
        this.itemNumber = itemNumber;
        this.quantity = Integer.parseInt(quantity);
        this.supplier = supplier;
        this.description = description;
    }

    // 构造函数
    public Goods(String itemNumber, int quantity, String supplier, String description) {
        this.itemNumber = itemNumber;
        this.quantity = quantity;
        this.supplier = supplier;
        this.description = description;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    // 获取属性函数
    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSupplier() {
        return supplier;
    }

    // 设置属性函数
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

class getInventory {
    ArrayList<Goods> goodsArray = new ArrayList<Goods>(); // 用于存储从inventory.txt中读取的货物信息
    BufferedWriter errorWriter = new BufferedWriter(new FileWriter("D:\\JavaApp\\OCR\\Experiment2\\Error.txt",true));

    Goods good = null;

    public getInventory() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("D:\\JavaApp\\OCR\\Experiment2\\Inventory.txt"));
        String line = null;
        while ((line = br.readLine()) != null) {
            String[] str = line.split("\\s+"); // 正则表达式，用于处理一个或者多个空格
            good = new Goods(str[0], str[1], str[2], str[3]);
            goodsArray.add(good);
        }
        br.close();

        // 以下部分将首先读取Transaction文件
        new getTransactions();

    }

    // O 出货，有四位，分别是o，货物编号，数量和客户 A在库存之中添加一种货物，分别是产品编号，供应商编号，描述。数量为零
    // R 到货，分别是产品编号和数量 D删除一种货物
    class getTransactions { // 此类用于从transactions.txt中读取数据并且处理

        ArrayList<String> transInform = new ArrayList<String>();
        ArrayList<String[]> splitedArrayList = new ArrayList<String[]>();
        ArrayList<String> shippingBuffer = new ArrayList<>();
        String line = null;
        String[] revtal = null;

        // 在读取Transaction的时候要对Arraylist之中的元素进行排序
        public getTransactions() throws Exception {
            BufferedReader readTrans = new BufferedReader(
                    new FileReader("D:\\JavaApp\\OCR\\Experiment2\\transactions.txt"));
            while ((line = readTrans.readLine()) != null) { // LINE每次读取一行数据
                transInform.add(line);
            }

            readTrans.close();

            //将transInform进行排序
            Collections.sort(transInform,new Comparator<String>() {

                @Override
                public int compare(String str, String str2) {
                    // TODO Auto-generated method stub
                    if (!(str.charAt(0)==str2.charAt(0))) {
                                            if (str.charAt(0) == 'O') {
                                                if (str2.charAt(0) == 'A') {
                                                    return 1;
                                                } else if (str2.charAt(0) == 'R') {
                                                    return -1;
                                                } else if (str2.charAt(0) == 'D') {
                                                    return -1;
                                                }
                                            } else if (str.charAt(0) == 'A') {
                                                return -1;
                                            } else if (str.charAt(0) == 'D') {
                                                return 2;
                                            } else if (str.charAt(0) == 'R') {
                                                if (str2.charAt(0) == 'O') {
                                                    return 1;
                                                } else if (str2.charAt(0) == 'D') {
                                                    return -1;
                                                } else if (str2.charAt(0) == 'A') {
                                                    return 1;
                                                }
                                            }
                                        } else {
                                            return (str.compareTo(str2)); // 如果相等，直接按字符串大小顺序进行比较即可
                                        }
            
                                    
                                return 0;
                            }
                
            });

            //将transInform按tab进行分割
            for(String temp:transInform){
                String []revtal2 = temp.split("\\s+");
                splitedArrayList.add(revtal2);
            }

            errorFileInfo();
            
            choose();

            WriteFile();
    }

    void errorFileInfo() throws IOException{
        errorWriter.write("库存不足信息以及删除货物信息:");
        errorWriter.newLine();
        errorWriter.write("数字分别代表：客户编号、货物编号及货物数量");
        errorWriter.newLine();
    }

    // 选择函数，判断究竟是出货、进货、还是添加货物、删除
    void choose() throws Exception {

    for(String[] temp1 :splitedArrayList){
        switch(temp1[0]){
            case "A":
                goodsAdd(temp1);
                break;
            case "O":
                goodsOutput(temp1);
                checkShipping();
                break;
            case "R":
                goodsReset(temp1);
                break;
            case "D":
                goodsDelete(temp1);
                break;
        }           

    }
}

    // 发货函数，需要将发货记录写到shipping.txt中
    void goodsOutput(String[] str) throws ShortageException, IOException {

        for (Goods good : goodsArray) { 

            if (str[1].equals(good.getItemNumber()) ) { 
                
                int num = Integer.parseInt(str[2]); 
                int qual = good.getQuantity();
                if (qual>num) {        //如果货物的数量大于发货的数量
                    good.setQuantity(qual - num);
                    String line = good.getSupplier() + "\t" + good.getItemNumber() + "\t"
                            + str[2];                     
                    shippingBuffer.add(line);
                }else {                   
                    String newLine1 = good.getSupplier() + "\t" + good.getItemNumber() + "\t"
                            + str[2];
                    errorWriter.write(newLine1);
                    errorWriter.flush();
                    errorWriter.newLine();
                }
            }
        }

    }

    // 添加货物函数
    void goodsAdd(String[] str) {
        Goods newGood = new Goods(str[1], 0, str[2], str[3]);
        goodsArray.add(newGood);
    }

    // 到货函数
    void goodsReset(String[] str) {
        for (Goods good : goodsArray) {
            if (str[1] == good.getItemNumber()) {
                good.setQuantity(good.getQuantity() + Integer.parseInt(str[2]));
            }
        }
    }

    // 删除货物
    void goodsDelete(String[] str) throws IOException {
        
        for (Goods good : goodsArray) {
            if (str[1] == good.getItemNumber()) {
                goodsArray.remove(good);
                boolean flag = goodsArray.remove(good);
                if((flag)&&good.getQuantity()>0){
                    String line = good.getSupplier() + "\t" + good.getItemNumber() + "\t" + Integer.toString(good.getQuantity());
                    errorWriter.write(line);
                    errorWriter.flush();
                    errorWriter.newLine();
                }
                
            }
        }
    }

    // 这是更新以后的库存记录，将其写道NewInventory.txt文件中
    void WriteFile() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\JavaApp\\OCR\\Experiment2\\NewInventory.txt"));
        for (Goods newGood : goodsArray) {
            String line = newGood.getItemNumber() + "\t" + Integer.toString(newGood.getQuantity()) + "\t"
                    + newGood.getSupplier() + "\t" + newGood.getDescription();
            bw.write(line);
            bw.newLine();
            bw.flush();
        }
        bw.close();

    }

   
    void checkShipping() throws Exception {
        BufferedWriter shippingWriter = new BufferedWriter(
            new FileWriter("D:\\JavaApp\\OCR\\Experiment2\\shipping.txt")) ;
            for (int i = 0; i < shippingBuffer.size() - 1; i++) { 
                for (int j = i + 1; j < shippingBuffer.size(); j++) {

                    //判断条件为：客户数量编号相同，并且货物编号相同
                    if ((shippingBuffer.get(i).toString().split("\t")[0]
                            .equals(shippingBuffer.get(j).toString().split("\t")[0]))
                            && (shippingBuffer.get(i).toString().split("\t")[2]
                                    .equals(shippingBuffer.get(j).toString().split("\t")[2]))) {
                        int num1 = Integer.parseInt(shippingBuffer.get(i).toString().split("\t")[2]);
                        int num2 = Integer.parseInt(shippingBuffer.get(j).toString().split("\t")[2]);
                        int num = num1 + num2;
                        String line = shippingBuffer.get(i).toString().split("\t")[0] + "\t"
                                + shippingBuffer.get(i).toString().split("\t")[1] + "\t" + Integer.toString(num);
                        shippingBuffer.remove(i); //将i和j位置的元素删除
                        shippingBuffer.remove(j);
                        shippingBuffer.add(i, line); //将合并后的元素添加到i位置
                        i--;
                    }

                }
            }
            //最后，将其写入文件中
            for (String sb : shippingBuffer) {
                shippingWriter.write(sb);
                shippingWriter.newLine();
                shippingWriter.flush();
            }

            shippingWriter.close();
        }
    }
}



class ShortageException extends Exception {
    public ShortageException() {
        super("库存不足");
    }
}

class CheckOutput {
    public static void checkOutput(int num) throws ShortageException {
        if (num > 0) {
            System.out.println("数据无误");
        } else {
            throw new ShortageException();
        }
    }
}