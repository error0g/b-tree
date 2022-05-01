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
		Map<String, Integer> expectMap = new HashMap<>();
		Random random = new Random();
		for (int i = 0; i < 1000000; i++) {
			String key=UUID.randomUUID().toString();
			Integer expect = random.nextInt(1000000);
			if (!expectMap.containsKey(key)) {
				expectMap.put(key, expect);
				bTree.add(key, expect);
			}
		}
		for (Map.Entry<String, Integer> expect : expectMap.entrySet()) {
			Assert.assertEquals(bTree.get(expect.getKey()), expect.getValue());
			bTree.remove(expect.getKey());
			Assert.assertNull(bTree.get(expect.getKey()));
		}
	}

	/**
	 *  100W随机读取获取
	 */
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

}
