package cn.error0;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class BTreeTest {

    /**
     * 100W随机数据新增
     */
    @Test
    public void addTest() {
        BTree<Integer> bTree = new BTree<>();
        Random random = new Random();
        for (int i = 0; i < 1000000; i++) {
            Integer expect = random.nextInt(1000000);
            bTree.add(String.valueOf(expect), expect);
            Integer result = bTree.get(String.valueOf(expect));
            Assert.assertEquals(result, expect);
        }
    }

    /**
     * 100W随机数据删除
     */
    @Test
    public void removeTest() {
        BTree<Integer> bTree = new BTree<>();
        Set<String> expectSet = new HashSet<>();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Integer expect = random.nextInt(11);
            if (!expectSet.contains(String.valueOf(expect))) {
                expectSet.add(String.valueOf(expect));
                bTree.add(String.valueOf(expect), expect);
            }
        }

        for (String item : expectSet) {
            bTree.remove(item);
            Integer result = bTree.get(item);
            Assert.assertNull(result);
        }
    }

    @Test
    public void getTest() {
        BTree<Integer> bTree = new BTree<>();
        Map<String, Integer> expectMap = new HashMap<>();
        Random random = new Random();
        for (int i = 0; i < 1000000; i++) {
            Integer expect = random.nextInt(1000000);
            String key = UUID.randomUUID().toString();
            bTree.add(key, expect);
            expectMap.put(key, expect);
        }
        for (Map.Entry<String, Integer> expect : expectMap.entrySet()) {
            Integer result = bTree.get(expect.getKey());
            Assert.assertEquals(result, expect.getValue());
        }
    }

    @Test
    public void main() {
        BTree<Integer> bTree = new BTree<>();
        bTree.add("1", 1);
        bTree.add("3", 3);
        bTree.add("4", 4);
        bTree.add("9", 9);
        bTree.add("10", 10);
        bTree.add("11", 11);
        bTree.add("12", 12);
        bTree.add("13", 13);
        bTree.remove("1");
        bTree.remove("3");
    }
}
