package cn.error0;

/**
 * @author yrx
 * @description
 * @date 2022/04/09 20:14
 **/
public class Main {
    public static void main(String[] args) {
        BTree<Integer> bTree = new BTree();
        long addStart = System.currentTimeMillis();
        for(int i=0;i<1000000;i++)
        {
            bTree.add(String.valueOf(i),i);
        }
        long addEnd = System.currentTimeMillis();
        long getStart = System.currentTimeMillis();
        for(int i=0;i<1000000;i++)
        {
            bTree.get(String.valueOf(i));
        }
        long getEnd = System.currentTimeMillis();

        System.out.println(addEnd-addStart);
        System.out.println(getEnd-getStart);
    }
}


