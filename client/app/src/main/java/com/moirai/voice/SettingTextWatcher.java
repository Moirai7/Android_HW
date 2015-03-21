package com.moirai.voice;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

public class SettingTextWatcher implements TextWatcher {
	private int editStart ;
	private int editEnd ;
	private EditTextPreference mEditTextPreference;
	int mType;
	private Context mContext;
	
	public SettingTextWatcher(Context context,EditTextPreference e,int t) {
		mContext = context;
		mEditTextPreference = e;
		mType = t;
	 }
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,int after) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		String sString = s.toString();
		int length = 0;
		editStart = sString.length();
		editEnd = sString.length();
		for(;length < sString.length();length++){
			if(sString.charAt(length) > '9' || sString.charAt(length) < '0'){
				Toast.makeText(mContext, "�����ַ�ͺ����ǲ��淶�ģ�", Toast.LENGTH_SHORT).show();
				s.delete(editStart-(sString.length()-length), editEnd-(sString.length()-length)+1);
				int tempSelection = editStart;
				mEditTextPreference.getEditText().setText(s);
				mEditTextPreference.getEditText().setSelection(tempSelection);
				break;
			}
			if(length > 0){
				if(sString.charAt(length) == '0' && sString.charAt(length-1) == '0'&& length == 1){
					s.delete(editStart-1, editEnd);
					int tempSelection = editStart;
					Toast.makeText(mContext, "С��鲻Ҫ����̫��Ѽ����!", Toast.LENGTH_SHORT).show();
					mEditTextPreference.getEditText().setText(s);
					mEditTextPreference.getEditText().setSelection(tempSelection);
					break;
				}
				else if(sString.charAt(length) != '0' && sString.charAt(length-1) == '0' && length == 1){
					s.delete(editStart-sString.length(), editEnd-sString.length()+1);
					Toast.makeText(mContext, "��һ�����ֲ����� 0 ����", Toast.LENGTH_SHORT).show();
					int tempSelection = editStart;
					mEditTextPreference.getEditText().setText(s);
					mEditTextPreference.getEditText().setSelection(tempSelection);
					break;
				}
			}
		}
		if(sString.length() == length && length >= 5){
			if(Integer.parseInt(sString) > 10000){
				switch (mType){
				case 1:
					new AlertDialog.Builder(mContext).setTitle("��Ч��ֵ")
					.setMessage("������Чֵ��Ĭ��Ϊ��4000").setPositiveButton("ȷ��",null).show();
					mEditTextPreference.getEditText().setText("4000");
					break;
					
				case 2:
					new AlertDialog.Builder(mContext).setTitle("��Ч��ֵ")
					.setMessage("������Чֵ��Ĭ��Ϊ��700").setPositiveButton("ȷ��",null).show();
					mEditTextPreference.getEditText().setText("700");
					break;
				case 3:
					new AlertDialog.Builder(mContext).setTitle("��Ч��ֵ")
					.setMessage("������Чֵ��Ĭ��Ϊ��5000").setPositiveButton("ȷ��",null).show();
					mEditTextPreference.getEditText().setText("5000");
					break;
					
				case 4:
					new AlertDialog.Builder(mContext).setTitle("��Ч��ֵ")
					.setMessage("������Чֵ��Ĭ��Ϊ��1800").setPositiveButton("ȷ��",null).show();
					mEditTextPreference.getEditText().setText("1800");
					break;
				}
			}
		}
		if(sString.length() == length && length >= 3 && mType == 5){
			if(Integer.parseInt(sString) > 100){
				new AlertDialog.Builder(mContext).setTitle("��Ч��ֵ")
				.setMessage("������Чֵ��Ĭ��Ϊ��50").setPositiveButton("ȷ��",null).show();
				mEditTextPreference.getEditText().setText("50");
			}
		}
	}
};
