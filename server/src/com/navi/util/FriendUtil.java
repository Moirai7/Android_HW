package com.navi.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.navi.model.RequestFriend;

public class FriendUtil {
	private List<RequestFriend> requests;
	private static FriendUtil friend = null;

	private FriendUtil() {
		requests = new ArrayList<RequestFriend>();
	}

	public static FriendUtil getInstance() {
		if (friend == null) {
			friend = new FriendUtil();
		}
		return friend;
	}

	public void addRequest(RequestFriend f) {
		requests.add(f);
	}

	public RequestFriend findRequest(String f) {
		Iterator<RequestFriend> iter = requests.iterator();
		while (iter.hasNext()) {
			RequestFriend str = (RequestFriend) iter.next();
			if (str.getSendName() == f)
				return str;
		}
		return null;
	}

	public void deleteRequest(String f) {
		RequestFriend friend = findRequest(f);
		requests.remove(friend);
	}

	private SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

	public String matchRequest(String strName) {
		Date date = new Date();
		Iterator<RequestFriend> iter = requests.iterator();
		while (iter.hasNext()) {
			RequestFriend str = (RequestFriend) iter.next();
			Date time;
			try {
				time = df.parse(str.getSendTime());
				long between = date.getTime() - time.getTime();
				long day1 = between / (24 * 3600);
				long hour1 = between % (24 * 3600) / 3600;
				long minute1 = between % 3600 / 60;
				if (day1 == 0 && hour1 == 0 && minute1 < 1
						&& str.getSendName() != strName) {
					return str.getSendName();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
