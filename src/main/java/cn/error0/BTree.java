package cn.error0;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;

/**
 * <p> B树的定义</p>
 * 1、每个结点属性
 * <blockquote>a. x.n，当前结点存储key数量</blockquote>
 * <blockquote>b. x.n个key关键字顺序存放 x.key[i]<x.key[i+1]</blockquote>
 * 2、每个结点包含x.n+1个子结点，叶子结点不包含。
 * <p> 2、每个结点包含x.n+1个子结点，叶子结点不包含。</p>
 * 3、T表示结点宽度，每个结点结点数量至少包含T-1，最大数量为2*T-1
 *
 * @author error0
 */
public class BTree<V> {
	private static final Integer DEFAULT_INITIAL_CAPACITY = 2;
	private Node<V> root;

	@Data
	@AllArgsConstructor
	private static class Element<V> implements Comparable<V> {
		private String key;
		private V value;

		@Override
		public int compareTo(Object o) {
			return key.compareTo(String.valueOf(o));
		}

		@Override
		public String toString() {
			return key;
		}
	}

	private static class Node<V> {
		//节点总数
		Integer num;
		//是否为叶子节点
		Boolean leaf;
		//元素
		Element<V>[] elements = new Element[2 * DEFAULT_INITIAL_CAPACITY - 1];
		//叶子节点
		Node<V>[] childNode = new Node[2 * DEFAULT_INITIAL_CAPACITY];

		@Override
		public String toString() {
			return Arrays.toString(elements);
		}
	}

	/**
	 * 根据key获取一个元素
	 *
	 * @param key
	 * @return
	 */
	public V get(String key) {
		return this.root == null ? null : this.search(this.root, key);
	}

	private V search(Node<V> node, String key) {
		int i = 0;
		for (; i < node.num && node.elements[i].compareTo(key) < 0; i++) ;
		if (i < node.num && key.equals(node.elements[i].getKey())) {
			return node.elements[i].getValue();
		} else if (node.leaf) {
			return null;
		} else {
			//递归寻找子树
			return this.search(node.childNode[i], key);
		}
	}

	public void remove(String key) {
		if (null == this.root)
			return;
		//特殊处理：当有2层结点，第三中情况需要跟root进行提前合并
		if (root.num == 1
				&& ((root.childNode[0] != null && root.childNode[0].num == 1)
				&& root.childNode[1] != null && root.childNode[1].num == 1)) {
			merge(root, 0, root.childNode[0], root.childNode[1]);
			remove(root, key);
			root = root.childNode[0];
		} else {
			this.remove(this.root, key);
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
	private void remove(Node<V> node, String key) {
		int i = 0;
		for (; i < node.num && node.elements[i].compareTo(key) < 0; i++) ;
		//1
		if (node.leaf) {
			if (key.equals(node.elements[i].getKey())) {
				for (int j = i; j < node.num - 1; j++) {
					node.elements[j] = node.elements[j + 1];
				}
				node.elements[node.num - 1] = null;
				node.num--;
			}
		} else {
			//2.a
			if (i < node.num && key.equals(node.elements[i].getKey())) {
				if (node.childNode[i].num >= DEFAULT_INITIAL_CAPACITY) {
					Node<V> leftNode = node.childNode[i];
					while (!leftNode.leaf) {
						leftNode = leftNode.childNode[leftNode.num];
					}
					String maxKey = leftNode.elements[leftNode.num - 1].getKey();
					node.elements[i] = leftNode.elements[leftNode.num - 1];
					this.remove(node.childNode[i], maxKey);
					//2.b
				} else if (node.childNode[i + 1].num >= DEFAULT_INITIAL_CAPACITY) {
					Node<V> rightNode = node.childNode[i + 1];
					while (!rightNode.leaf) {
						rightNode = rightNode.childNode[0];
					}
					String miniKey = rightNode.elements[0].getKey();
					node.elements[i] = rightNode.elements[0];
					this.remove(node.childNode[i+1], miniKey);
					//2.c
				} else {
					this.merge(node, i, node.childNode[i], node.childNode[i + 1]);
					this.remove(node.childNode[i], key);
				}
			} else {
				//第三种情况
				Node<V> leftChild = node.childNode[i];
				Node<V> rightChild = null;
				if (i < node.num) {
					rightChild = node.childNode[i + 1];
				}
				if (leftChild.num == DEFAULT_INITIAL_CAPACITY - 1) {
					Node<V> prev = null;
					if (i > 0) {
						prev = node.childNode[i - 1];
					}
					//3.a
					if (i > 0 && prev.num >= DEFAULT_INITIAL_CAPACITY) {
						prevShiftLeft(node, i - 1, prev, leftChild);
					}
					//3.b
					else if (i < node.num && rightChild.num >= DEFAULT_INITIAL_CAPACITY) {
						this.rightShiftLeft(node, i, leftChild, rightChild);
					}
					//3.c
					else {
						if (i > 0) {
							merge(node, i - 1, prev, leftChild);
							leftChild = prev;
						} else {
							merge(node, i, leftChild, rightChild);
						}
					}
				}
				this.remove(leftChild, key);
			}
		}
	}

	/**
	 * @param father    x
	 * @param prev      c[i-1]
	 * @param leftChild c[i]
	 */
	private void prevShiftLeft(Node<V> father, int index, Node<V> prev, Node<V> leftChild) {
		//index:i-1
		for (int i = leftChild.num; i > 0; i--) {
			leftChild.elements[i] = leftChild.elements[i - 1];
		}
		leftChild.elements[0] = father.elements[index];
		leftChild.num++;

		if (!prev.leaf) {
			for (int i = leftChild.num; i > 0; i--) {
				leftChild.childNode[i] = leftChild.childNode[i - 1];
			}
			leftChild.childNode[0] = prev.childNode[prev.num];
			prev.childNode[prev.num] = null;
		}

		father.elements[index] = prev.elements[prev.num - 1];
		prev.elements[prev.num - 1] = null;
		prev.num--;
	}

	/**
	 * @param father     x
	 * @param leftChild  c[i]
	 * @param rightChild c[i+1]
	 */
	private void rightShiftLeft(Node<V> father, int index, Node<V> leftChild, Node<V> rightChild) {
		leftChild.elements[leftChild.num] = father.elements[index];
		leftChild.num++;

		father.elements[index] = rightChild.elements[0];
		for (int i = 0; i < rightChild.num - 1; i++) {
			rightChild.elements[i] = rightChild.elements[i + 1];
		}
		rightChild.elements[rightChild.num - 1] = null;
		if (!rightChild.leaf) {
			leftChild.childNode[leftChild.num] = rightChild.childNode[0];
			for (int i = 0; i < rightChild.num; i++) {
				rightChild.childNode[i] = rightChild.childNode[i + 1];
			}
			rightChild.childNode[rightChild.num] = null;
		}
		rightChild.num--;

	}

	/**
	 * @param father
	 * @param index
	 */
	private void merge(Node<V> father, int index, Node<V> left, Node<V> right) {
		//剪贴方式合并x[i]至left
		left.elements[DEFAULT_INITIAL_CAPACITY - 1] = father.elements[index];
		left.num++;
		for (int i = index; i < father.num - 1; i++) {
			father.elements[i] = father.elements[i + 1];
		}
		father.elements[father.num - 1] = null;

		for (int i = DEFAULT_INITIAL_CAPACITY; i < 2 * DEFAULT_INITIAL_CAPACITY - 1; i++) {
			left.elements[i] = right.elements[i - DEFAULT_INITIAL_CAPACITY];
			left.num++;
		}

		if (!left.leaf) {
			for (int i = DEFAULT_INITIAL_CAPACITY; i < 2 * DEFAULT_INITIAL_CAPACITY; i++) {
				left.childNode[i] = right.childNode[i - DEFAULT_INITIAL_CAPACITY];
			}
		}
		for (int i = index + 1; i < father.num; i++) {
			father.childNode[i] = father.childNode[i + 1];
		}
		father.childNode[father.num] = null;
		father.num--;
	}

	/**
	 * @param key;
	 * @param value
	 */
	public void add(String key, V value) {
		if (null == root) {
			create(key, value);
			return;
		}
		if (root.num == DEFAULT_INITIAL_CAPACITY * 2 - 1) {
			Node<V> newRoot = new Node<>();
			newRoot.leaf = Boolean.FALSE;
			newRoot.childNode[0] = root;
			newRoot.num = 0;
			root = newRoot;
			splitChild(root, 0);
		}
		this.insert(root, key, value);

	}

	/**
	 * 初始化root
	 *
	 * @param key
	 * @param value
	 */
	private void create(String key, V value) {
		root = new Node<V>();
		root.elements[0] = new Element<>(key, value);
		root.leaf = Boolean.TRUE;
		root.num = 1;
	}

	/**
	 * @param father
	 * @param index
	 */
	private void splitChild(Node<V> father, int index) {
		Node<V> needSplit = father.childNode[index];
		Node<V> right = new Node<V>();
		right.leaf = needSplit.leaf;
		right.num = DEFAULT_INITIAL_CAPACITY - 1;

		//复制需要分裂的节点Key
		for (int i = 0; i < DEFAULT_INITIAL_CAPACITY - 1; i++) {
			right.elements[i] = needSplit.elements[DEFAULT_INITIAL_CAPACITY + i];
			needSplit.elements[DEFAULT_INITIAL_CAPACITY + i] = null;
		}

		//如果不是叶子节点复制子节点
		if (!needSplit.leaf) {
			for (int i = 0; i < DEFAULT_INITIAL_CAPACITY; i++) {
				right.childNode[i] = needSplit.childNode[DEFAULT_INITIAL_CAPACITY + i];
				needSplit.childNode[DEFAULT_INITIAL_CAPACITY + i] = null;
			}
		}
		needSplit.num = DEFAULT_INITIAL_CAPACITY - 1;

		//key转移到父节点
		for (int i = father.num - 1; i >= index; i--) {
			father.elements[i + 1] = father.elements[i];
		}

		father.elements[index] = needSplit.elements[DEFAULT_INITIAL_CAPACITY - 1];
		needSplit.elements[DEFAULT_INITIAL_CAPACITY - 1] = null;

		//子节点迁移至父节点中
		for (int i = father.num; i > index; i--) {
			father.childNode[i + 1] = father.childNode[i];
		}
		father.childNode[index + 1] = right;
		needSplit.childNode[DEFAULT_INITIAL_CAPACITY] = null;
		father.num++;
	}

	/**
	 * <p>新结点需要插入至叶子结点</p>
	 * 插入节点寻找叶子结点位置时，如果子结点已经满了(node.num>2*T-1)需要进行分裂。
	 *
	 * @param node
	 * @param key
	 * @param value
	 */
	private void insert(Node<V> node, String key, V value) {
		int i = node.num - 1;
		if (node.leaf) {
			while (i >= 0 && node.elements[i].compareTo(key) > 0) {
				node.elements[i + 1] = node.elements[i];
				i--;
			}
			//纠正插入位置
			i++;
			node.elements[i] = new Element<V>(key, value);
			node.num++;
		} else {
			while (i >= 0 && node.elements[i].compareTo(key) > 0) {
				i--;
			}
			//纠正位置
			i++;
			if (node.childNode[i].num == 2 * DEFAULT_INITIAL_CAPACITY - 1) {
				splitChild(node, i);
				if (node.elements[i].compareTo(key) < 0) {
					i++;
				}
			}
			insert(node.childNode[i], key, value);
		}
	}
}
