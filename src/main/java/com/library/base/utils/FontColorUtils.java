package com.library.base.utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StyleSpan;

/**
 * 设置文本颜色与加粗
 */
public class FontColorUtils {
	/**
	 * 给字体加上颜色
	 * 
	 * @param colorCode 例如：#ffffff
	 * @param text
	 * @return
	 */
	public static SpannableString addColor(String colorCode, String text) {
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new ForegroundColorSpan(Color.parseColor(colorCode)), 0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanString;
	}

	/**
	 * 给字体加上颜色
	 * 
	 * @param color 例如:0x00000000
	 * @param text
	 * @return
	 */
	public static SpannableString addColor(int color, String text) {
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new ForegroundColorSpan(color), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanString;
	}
	/**
	 * 调整字体大小 （相对值,单位：像素）
	 * @param relativeSice 倍数
	 * @param text
	 * @return
	 */
	public static SpannableString resize(float relativeSice, String text) {
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new RelativeSizeSpan(relativeSice), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanString;
	}
	/**
	 * 字体宽度
	 * @param relativeSice 宽度倍数
	 * @param text
	 * @return
	 */
	public static CharSequence scaleX(float relativeSice, String text) {
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new ScaleXSpan(relativeSice), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanString;
	}


	/**
	 * 设置字体粗体
	 * @param text
     * @return
     */
	public static CharSequence setBold(String text) {
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanString;
	}


	public static class Builder{
		SpannableString spanString;
		int length = 0;
		public Builder(String txt){
			this.spanString = new SpannableString(txt);
			this.length = txt.length();
		}

		public Builder addColor(int color){
			addColor(color,0,length);
			return this;
		}

		public Builder addColor(int color,int length){
			addColor(color,0,length);
			return this;
		}

		public Builder addColor(int color,int beg,int length){
			spanString.setSpan(new ForegroundColorSpan(color), beg, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			return this;
		}


		public Builder addColor(String colorCode,int length){
			addColor(Color.parseColor(colorCode),length);
			return this;
		}

		public Builder addColor(String colorCode,int beg,int length){
			addColor(Color.parseColor(colorCode),beg,length);
			return this;
		}


		public Builder addColor(String colorCode){
			addColor(Color.parseColor(colorCode));
			return this;
		}


		public Builder bold(){
			spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			return this;
		}

		public Builder bold(int length){
			spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			return this;
		}


		public Builder bold(int beg,int length){
			spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), beg, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			return this;
		}

		public SpannableString build(){
			return spanString;
		}

	}


}