package cn.error0;

import java.util.Arrays;

/**
 * @author error0
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
        for (; i < node.num && key.compareTo(node.key[i]) > 0; i++) ;
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
     * 删除节点的几种情况
     * <p>1、 如果key存在x节点中，并且是[叶节点]直接从x删除key。</p>
     * <p>2、 如果key存在x节点中，并且是[内部节点]。</p>
     * <blockquote>a.如果x的左子节点y至少包含t个关键字则找出y的(最大的)最右的关键字key'替换key，并在y中递归删除key'</blockquote>
     * <blockquote><pre>b.如果x的右子节点z至少包含t个关键字则找出z的（最小的）最左的关键字key'替换key，并在z中递归删除key'</pre></blockquote>
     * <blockquote><pre>c.否者如果y和z都只有t-1个关键字，则将key与z合并到y中，使得y有2t-1个关键字，再从y中递归删除key</pre></blockquote>
     * <p>3、如果key不存在x节点中，必然在x的某个子节点。如果childNode[i]子节点只有t-1个关键字</p>
     * <blockquote><pre>a.如果childNode[i-1] 拥有至少t个关键字，则将x的某个关键字降至childNode[i],childNode[i-1]的最大关键字提升到x</pre></blockquote>
     * <blockquote><pre>b.如果childNode[i+1] 拥有至少t个关键字，则将x的某个关键字降至childNode[i],childNode[i+1]的最大关键字提升到x</pre></blockquote>
     * <blockquote><pre>c.如果childNode[i-1]和childNode[i+1]都只拥有t-1个关键字，则将childNode[i]与其中一个合并，将x的一个关键之将到新合并的节点中成为中间关键字。</pre></blockquote>
     *
     * @param key
     */
    public void delete(String key) {
        if (null == root)
            return;
        delete(root, key);
    }

    private void delete(Node node, String key) {
        int i = 0;
        for (; i < node.num && key.compareTo(node.key[i]) > 0; i++) ;
        //情况1
        if (node.leaf) {
            if (key.equals(node.key[i])) {
                for (int j = i; j < node.num - 1; j++) {
                    node.key[j] = node.key[j + 1];
                    node.value[j] = node.value[j + 1];
                }
                node.key[node.num-1] = null;
                node.value[node.num-1] = null;
                node.num--;
            }
        } else {
            //情况2.a
            if (key.equals(node.key[i])) {
                if (node.childNode[i].num >= T) {
                    Node leftNode = node.childNode[i];
                    while (!leftNode.leaf) {
                        leftNode = leftNode.childNode[leftNode.num];
                    }
                    String maxKey = node.key[leftNode.num - 1];
                    V value = (V) node.value[leftNode.num - 1];
                    node.key[i] = maxKey;
                    node.value[i] = value;
                    delete(leftNode, maxKey);
                    leftNode.num--;
                    //情况2.b
                } else if (node.childNode[i + 1].num >= T) {
                    Node rightNode = node.childNode[i + 1];
                    while (!rightNode.leaf) {
                        rightNode = rightNode.childNode[0];
                    }
                    String miniKey = rightNode.key[0];
                    V value = (V) rightNode.value[0];
                    node.key[i] = miniKey;
                    node.value[i] = value;
                    delete(rightNode, miniKey);
                    rightNode.num--;
                    //情况2.c
                } else {
                    merge(node, i);
                    delete(node.childNode[i], key);
                }
            } else {

            }
        }
    }

    private void merge(Node x, int index) {
        Node y = x.childNode[index];
        Node z = x.childNode[index + 1];
        String key = x.key[index];
        V value = (V) x.value[index];

        y.key[T - 1] = key;
        y.value[T - 1] = value;
        y.num++;

        for (int i = T; i < 2 * T - 1; i++) {
            y.key[i] = z.key[i - T];
            y.value[i] = z.value[i - T];
            y.num++;
        }
        if (!z.leaf) {
            for (int i = T; i < 2 * T; i++) {
                y.childNode[i] = z.childNode[i - T];
            }
        }

        for (int i = index + 1; i < root.num; i++) {
            x.childNode[i] = x.childNode[i + 1];
        }
    }


    /**
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
