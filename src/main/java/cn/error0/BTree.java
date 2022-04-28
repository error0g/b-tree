package cn.error0;

import java.util.Arrays;

/**
 * @author error0
 * @description
 * @date 2022/04/09 20:49
 **/
public class BTree<V> {
    /**
     * <p> B树的定义</p>
     * 1、每个结点属性
     * <blockquote>a. x.n，当前结点存储key数量</blockquote>
     * <blockquote>b. x.n个key关键字顺序存放 x.key[i]<x.key[i+1]</blockquote>
     * 2、每个结点包含x.n+1个子结点，叶子结点不包含。
     * <p> 2、每个结点包含x.n+1个子结点，叶子结点不包含。</p>
     * 3、T表示结点宽度，每个结点结点数量至少包含T-1，最大数量为2*T-1
     */
    private static final Integer T = 2;
    private Node<V> root;

    private static class Node<V> {

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
            return Arrays.toString(key);
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

    private V treeSearch(Node<V> node, String key) {
        int i = 0;
        for (; i < node.num && key.compareTo(node.key[i]) > 0; i++) ;
        if (i < node.num && key.equals(node.key[i])) {
            return node.value[i];
        } else if (node.leaf) {
            return null;
        } else {
            return treeSearch(node.childNode[i], key);
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
     * <blockquote><pre>b.如果childNode[i+1] 拥有至少t个关键字，则将x的某个关键字降至childNode[i],childNode[i+1]的最小关键字提升到x</pre></blockquote>
     * <blockquote><pre>c.如果childNode[i-1]和childNode[i+1]都只拥有t-1个关键字，则将childNode[i]与其中一个合并，将x的一个关键之将到新合并的节点中成为中间关键字。</pre></blockquote>
     *
     * @param key
     */
    public void delete(String key) {
        if (null == root)
            return;
        delete(root, key);
    }

    private void delete(Node<V> node, String key) {
        int i = 0;
        for (; i < node.num && key.compareTo(node.key[i]) > 0; i++) ;
        //1
        if (node.leaf) {
            if (key.equals(node.key[i])) {
                for (int j = i; j < node.num - 1; j++) {
                    node.key[j] = node.key[j + 1];
                    node.value[j] = node.value[j + 1];
                }
                node.key[node.num - 1] = null;
                node.value[node.num - 1] = null;
                node.num--;
            }
        } else {
            //2.a
            if (i < node.num && key.equals(node.key[i])) {
                if (node.childNode[i].num >= T) {
                    Node<V> leftNode = node.childNode[i];
                    while (!leftNode.leaf) {
                        leftNode = leftNode.childNode[leftNode.num];
                    }
                    String maxKey = node.key[leftNode.num - 1];
                    V value = node.value[leftNode.num - 1];
                    node.key[i] = maxKey;
                    node.value[i] = value;
                    delete(leftNode, maxKey);
                    leftNode.num--;
                    //2.b
                } else if (node.childNode[i + 1].num >= T) {
                    Node<V> rightNode = node.childNode[i + 1];
                    while (!rightNode.leaf) {
                        rightNode = rightNode.childNode[0];
                    }
                    String miniKey = rightNode.key[0];
                    V value = rightNode.value[0];
                    node.key[i] = miniKey;
                    node.value[i] = value;
                    delete(rightNode, miniKey);
                    rightNode.num--;
                    //2.c
                } else {
                    merge(node, i);
                    delete(node.childNode[i], key);
                }
            } else {
                //第三种情况
                Node<V> leftChild = node.childNode[i];
                Node<V> rightChild = null;
                if (i < node.num) {
                    rightChild = node.childNode[i + 1];
                }
                if (leftChild.num == T - 1) {
                    Node<V> prev = null;
                    if (i > 0) {
                        prev = node.childNode[i - 1];
                    }
                    //3.a
                    if (i > 0 && prev.num >= T) {
                        prevShiftLeft(node, i, prev, leftChild);
                        leftChild = prev;
                    }
                    //3.b
                    else if (i < node.num && rightChild.num >= T) {
                        rightShiftLeft(node, i, leftChild, rightChild);
                    }
                    //3.c
                    else {
                        merge(node, i);
                    }
                }
                delete(leftChild, key);
            }
        }
    }

    /**
     * @param father    x
     * @param prev      c[i-1]
     * @param leftChild c[i]
     */
    private void prevShiftLeft(Node<V> father, int index, Node<V> prev, Node<V> leftChild) {
        String fatherKey = father.key[index];
        V fatherValue = father.value[index];

        //c[i-1]移动至x
        while (!prev.leaf) {
            prev = prev.childNode[prev.num];
        }
        for (int i = father.num - 1; i > 1; i--) {
            father.key[i] = father.key[i - 1];
            father.value[i] = father.value[i - 1];
        }
        father.key[0] = prev.key[prev.num - 1];
        father.value[0] = prev.value[prev.num - 1];
        prev.key[prev.num - 1] = null;
        prev.value[prev.num - 1] = null;
        prev.num--;

        //x移动至c[i]
        leftChild.key[leftChild.num - 1] = fatherKey;
        leftChild.value[leftChild.num - 1] = fatherValue;
        leftChild.num++;
    }

    /**
     * @param father     x
     * @param leftChild  c[i]
     * @param rightChild c[i+1]
     */
    private void rightShiftLeft(Node<V> father, int index, Node<V> leftChild, Node<V> rightChild) {
        String fatherKey = father.key[index];
        V fatherValue = (V) father.value[index];

        //x移动至c[i]
        leftChild.key[leftChild.num] = fatherKey;
        leftChild.value[leftChild.num] = fatherValue;
        leftChild.num++;
        for (int i = index; i < father.num - 1; i++) {
            father.key[i] = father.key[i];
            father.value[i] = father.value[i];
        }
        father.num--;

        //c[i+1]移动至x
        while (!rightChild.leaf) {
            rightChild = rightChild.childNode[0];
        }
        father.key[father.num] = rightChild.key[0];
        father.value[father.num] = rightChild.value[0];

        for (int i = 0; i < rightChild.num - 1; i++) {
            rightChild.key[i] = rightChild.key[i + 1];
            rightChild.value[i] = rightChild.value[i + 1];
        }

        rightChild.num--;
        rightChild.key[rightChild.num] = null;
        rightChild.value[rightChild.num] = null;
    }

    private void merge(Node<V> father, int index) {
        Node<V> left = father.childNode[index];
        Node<V> right = father.childNode[index + 1];
        String key = father.key[index];
        V value = father.value[index];

        left.key[T - 1] = key;
        left.value[T - 1] = value;
        left.num++;

        for (int i = T; i < 2 * T - 1; i++) {
            left.key[i] = right.key[i - T];
            left.value[i] = right.value[i - T];
            left.num++;
        }
        if (!right.leaf) {
            for (int i = T; i < 2 * T; i++) {
                left.childNode[i] = right.childNode[i - T];
            }
        }

        for (int i = index + 1; i < root.num; i++) {
            father.childNode[i] = father.childNode[i + 1];
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
        root = new Node<V>();
        root.key[0] = key;
        root.value[0] = value;
        root.leaf = Boolean.TRUE;
        root.num = 1;
    }

    private void splitChild(Node<V> father, int index) {
        Node<V> needSplit = father.childNode[index];
        Node<V> right = new Node<V>();
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
        for (int i = father.num - 1; i >= index; i--) {
            father.key[i + 1] = father.key[i];
            father.value[i + 1] = father.value[i];
        }

        father.key[index] = needSplit.key[T - 1];
        father.value[index] = needSplit.value[T - 1];
        needSplit.key[T - 1] = null;

        //子节点迁移至父节点中
        for (int i = father.num; i > index; i--) {
            father.childNode[i + 1] = father.childNode[i];
        }
        father.childNode[index + 1] = right;
        needSplit.childNode[T] = null;
        father.num++;
    }

    private void insertNotFull(Node<V> node, String key, V value) {
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
