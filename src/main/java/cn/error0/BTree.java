package cn.error0;

/**
 * @author yrx
 * @description
 * @date 2022/04/09 20:49
 **/
public class BTree<V> {

	private static final Integer T = 2;
	private Node root;

	private class Node<V> {

		String key[] = new String[2 * T - 1];
		V value[] = (V[]) new Object[2 * T - 1];

		//节点总数
		Integer num;
		//是否为叶子节点
		Boolean leaf;
		//叶子节点
		Node<V> childNode[] = new Node[2 * T];

	}

	public V get(String key) {
		if (null == root)
			return null;
		return treeSearch(root, key);
	}

	private V treeSearch(Node node, String key) {
		int i;
		for (i = 0; i < node.num && key.compareTo(node.key[i]) > 0; i++) ;
		if (i < node.num && key.equals(node.key[i])) return (V) node.value[i];
		else if (node.leaf) return null;
		else return (V) treeSearch(node.childNode[i], key);
	}


	public void delete(String key) {

	}

	/**
	 * 新增root
	 *
	 * @param key
	 * @param value
	 */
	public void add(String key, V value) {
		if (null == root)
			create(key, value);


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
	 * 节点分裂
	 *
	 * @param node  未满的内部父节点
	 * @param index 已满的子节点下标
	 */
	private void splitChild(Node node, int index) {
		Node needSplit = node.childNode[index];
		Node right = new Node();
		right.leaf = needSplit.leaf;
		right.num = T - 1;

		//复制需要分裂的节点Key
		for (int i = 0; i < T - 1; i++) {
			right.key[i] = needSplit.key[T + i];
		}

		//如果不是叶子节点复制子节点
		if (!needSplit.leaf) {
			for (int i = 0; i < T; i++) {
				right.childNode[i] = needSplit.childNode[T + i];
			}
		}

		needSplit.num = T - 1;

	}


}
