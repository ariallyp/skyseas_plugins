package com.skyseas.openfireplugins.chatlogs;

import java.util.List;

import redis.clients.jedis.Jedis;

public class Test {

	public static void main(String[] args) throws Exception {

		JedisManager jedisUtil = JedisManager.getInstance();
		Jedis jedis = jedisUtil.getJedis();

		String key = "chatlogs";
		int i=0;
		while (true) {
			i++;
			// block invoke
			List<String> msgs = jedis.brpop(0, key);
			System.out.println(i);
			if (msgs == null)
				continue;
			String jobMsg = msgs.get(1);
			System.out.println(jobMsg);

		}
		
	
//		  String s; while( (s = jedis.rpop("chatlogs"))!=null){
//		  System.out.println(s); } System.out.println("结束");
//		  jedisUtil.closeJedis(jedis);
		 

		// ChatLogs c = new ChatLogs(1L, "luqs@localhost/Spark 2.6.3", new
		// Timestamp(new Date().getTime()), "hi", null, 2, 0);
		// c.setDetail("<message id=\"LxKvG-66\" to=\"admin@localhost\" from=\"luqs@localhost/Spark 2.6.3\" type=\"chat\"><body>hi</body><thread>q8o4C0</thread><x xmlns=\"jabber:x:event\"><offline/><composing/></x></message>");
		// String json = new Gson().toJson(c);
		// System.out.println(json);
		//
		// System.out.println(new Gson().fromJson(json, ChatLogs.class));

		// messageId:1 sessionJID:luqs@localhost/Spark 2.6.3 sender:luqs
		// receiver:admin createDate:2014-12-16 11:12:27.415 content:hi
		// detail:<message id="LxKvG-66" to="admin@localhost"
		// from="luqs@localhost/Spark 2.6.3"
		// type="chat"><body>hi</body><thread>q8o4C0</thread><x
		// xmlns="jabber:x:event"><offline/><composing/></x></message> length:2
		// state:0

	}

}
