package cn.error0;

import java.util.Arrays;

/**
 * @author yrx
 * @description
 * @date 2022/04/09 20:49
 **/
public class BTree<V> {

    private static final Integer T = 2;
    private Node root;

    private class Node<V> {

        String[] key = new String[2 * T - 1];
        V[] value = (V[]) new Object[2 * T - 1];

        //节点总数
        Integer num;
        //是否为叶子节点
        Boolean leaf;
        //叶子节点
        Node<V>[] childNode = new Node[2 * T];

        @Override
        public String toString() {
            return "Node{" +
                    "key=" + Arrays.toString(key) + '}';
        }
    }

    /**
     * 根据key获取一个元素
     *
     * @param key
     * @return
     */
    public V get(String key) {
        if (null == root) {
            return null;
        }
        return treeSearch(root, key);
    }

    private V treeSearch(Node node, String key) {
        int i = 0;
        while (i < node.num && key.compareTo(node.key[i]) > 0) {
            i++;
        }
        if (i < node.num && key.equals(node.key[i])) {
            return (V) node.value[i];
        } else if (node.leaf) {
            return null;
        } else {
            return (V) treeSearch(node.childNode[i], key);
        }
    }

    /**
     * 根据key删除元素
     *
     * @param key
     */
    public void delete(String key) {

    }

    /**
     * 新增root
     *
     * @param key
     * @param value
     */
    public void add(String key, V value) {
        if (null == root) {
            create(key, value);
            return;
        }
        if (root.num == T * 2 - 1) {
            Node<V> newRoot = new Node<>();
            newRoot.leaf = Boolean.FALSE;
            newRoot.childNode[0] = root;
            newRoot.num = 0;
            root = newRoot;
            splitChild(root, 0);
        }
        insertNotFull(root, key, value);

    }

    /**
     * 初始化root
     *
     * @param key
     * @param value
     */
    private void create(String key, V value) {
        root = new Node();
        root.key[0] = key;
        root.value[0] = value;
        root.leaf = Boolean.TRUE;
        root.num = 1;
    }

    /**
     * 节点分裂,条件达到node.c[index].n==2*T-1
     *
     * @param fatherNode 未满的内部父节点
     * @param index      已满的子节点下标
     */
    private void splitChild(Node fatherNode, int index) {
        Node needSplit = fatherNode.childNode[index];
        Node right = new Node();
        right.leaf = needSplit.leaf;
        right.num = T - 1;

        //复制需要分裂的节点Key
        for (int i = 0; i < T - 1; i++) {
            right.key[i] = needSplit.key[T + i];
            right.value[i] = needSplit.value[T + i];
            needSplit.key[T + i] = null;
            needSplit.value[T + i] = null;
        }

        //如果不是叶子节点复制子节点
        if (!needSplit.leaf) {
            for (int i = 0; i < T; i++) {
                right.childNode[i] = needSplit.childNode[T + i];
                needSplit.childNode[T + i] = null;
            }
        }
        needSplit.num = T - 1;

        //key转移到父节点
        for (int i = fatherNode.num - 1; i >= index; i--) {
            fatherNode.key[i + 1] = fatherNode.key[i];
            fatherNode.value[i + 1] = fatherNode.value[i];
        }

        fatherNode.key[index] = needSplit.key[T - 1];
        fatherNode.value[index] = needSplit.value[T - 1];
        needSplit.key[T - 1] = null;

        //子节点迁移至父节点中
        for (int i = fatherNode.num; i > index; i--) {
            fatherNode.childNode[i + 1] = fatherNode.childNode[i];
        }
        fatherNode.childNode[index + 1] = right;
        needSplit.childNode[T] = null;
        fatherNode.num++;
    }

    private void insertNotFull(Node node, String key, V value) {
        int i = node.num - 1;
        if (node.leaf) {
            while (i >= 0 && key.compareTo(node.key[i]) < 0) {
                node.key[i + 1] = node.key[i];
                node.value[i + 1] = node.value[i];
                i--;
            }
            node.num++;
            //纠正插入位置
            i++;
            node.key[i] = key;
            node.value[i] = value;
        } else {
            while (i >= 0 && key.compareTo(node.key[i]) < 0) {
                i--;
            }
            //纠正位置
            i++;
            if (node.childNode[i].num == 2 * T - 1) {
                splitChild(node, i);
                if (key.compareTo(node.key[i]) > 0) {
                    i++;
                }
            }
            insertNotFull(node.childNode[i], key, value);
        }

    }
}
