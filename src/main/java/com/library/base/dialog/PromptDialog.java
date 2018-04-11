package com.library.base.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.library.base.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @describe:
 * @author: zhengjl
 * @date: 2016/9/20 0020
 */
public class PromptDialog extends BaseDialog {


    private Button okBtn;
    private View cancelBtn;
    private TextView contentView;
    private TextView title;
    private EditText inputView;
    private View divider;
    private TextView contentViewV2;

    private View titleLayout;

    private int type;
    public static final int TEXTDIALOG = 1;
    public static final int INPUTDIALOG = 2;
    private boolean mustNumber;
    private boolean mustLetter;
    private int maxNumber = 10;
    private int minNumber = 1;
    private Context context;


    public PromptDialog(Context context) {
        super(context, R.layout.fragment_dialog_prompt);
        this.context = context;
    }

    public PromptDialog(Context context, int layoutId) {
        super(context, layoutId);
        this.context = context;
    }

    @Override
    protected void initDialog() {
        okBtn = (Button) findViewById(R.id.ok_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        contentView = (TextView) findViewById(R.id.dialog_operate_content);
        contentViewV2 = (TextView) findViewById(R.id.dialog_operate_content_v2);

        inputView = (EditText) findViewById(R.id.dialog_operate_input);
        title = (TextView) findViewById(R.id.title);
        titleLayout = findViewById(R.id.rl_prompt_dialog_title);
        divider = findViewById(R.id.divider);
        setTitle("提示");
        setDialogType(TEXTDIALOG);
    }

    /**
     * 标题是否可见
     *
     * @param visibility
     */
    public void setTitleVisibility(int visibility) {
        titleLayout.setVisibility(visibility);
    }

    @Override
    protected void setListener() {
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ok_btn) {
            if (type == TEXTDIALOG) {
                if (callBack != null)
                    callBack.callBack(true);
                dismiss();
                return;
            }
            if (inputView.getText().toString().trim().equals("")) {
                showToast("请输入内容");
                return;
            }
            if (inputView.getText().toString().trim().length() < minNumber) {
                showToast("输入内容不能少于" + minNumber + "个字符");
                return;
            }
            if (inputView.getText().toString().length() > maxNumber) {
                showToast("输入内容不能超过" + maxNumber + "个字符");
                return;
            }
            if (isFalse())
                return;
            if (callBack != null)
                callBack.callBack(inputView.getText().toString());
            dismiss();

        } else if (i == R.id.cancel_btn) {
            if (callBack != null)
                callBack.cancel(true);
            dismiss();

        }
    }

    private boolean isFalse() {
        if (mustNumber || mustLetter) {
            String txt = inputView.getText().toString().trim();
            if (mustNumber && mustLetter) {
                Pattern p = Pattern.compile("^[A-Za-z0-9]+$");
                Matcher m = p.matcher(txt);
                if (!m.matches()) {
                    showToast("输入的必需是字母或数字");
                    return true;
                }
            } else if (mustNumber && !mustLetter) {
                Pattern p = Pattern.compile("^[0-9]*$");
                Matcher m = p.matcher(txt);
                if (!m.matches()) {
                    showToast("输入必需是数字");
                    return true;
                }
            } else if (!mustNumber && mustLetter) {
                Pattern p = Pattern.compile("^[A-Za-z]+$");
                Matcher m = p.matcher(txt);
                if (!m.matches()) {
                    showToast("输入必需是字母");
                    return true;
                }
            }
        }
        return false;
    }

    public void cleanInput() {
        inputView.setText("");
    }

    public void setInputContent(String content) {
        if (content == null) return;
        inputView.setText(content);
        inputView.setSelection(content.length());
    }

    public void setOkText(String text) {
        okBtn.setText(text);
    }

    public void setCancelBtn(String text) {
        try {
            Button cancle = (Button) cancelBtn;
            cancle.setText(text);
        } catch (Exception e) {

        }
    }

    public void setCancelButtonVisible(boolean visible) {
        if (visible) {
            cancelBtn.setVisibility(View.VISIBLE);
            return;
        }
        cancelBtn.setVisibility(View.GONE);
        divider.setVisibility(View.GONE);
        okBtn.setBackgroundResource(R.drawable.bg_btn_dialog_left_right);
    }

    public void setOKVisible(boolean visible) {
        okBtn.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * 对话框类型
     *
     * @param type 文本框 ：TEXTDIALOG
     *             输入框 ：INPUTDIALOG
     */
    public void setDialogType(int type) {
        this.type = type;
        if (type == TEXTDIALOG) {
            contentView.setVisibility(View.VISIBLE);
            inputView.setVisibility(View.GONE);
        } else {
            inputView.setVisibility(View.VISIBLE);
            contentView.setVisibility(View.GONE);
        }
    }

    /**
     * @param mustNumber
     * @param mustLetter
     * @param maxNumber
     */
    public void setInputType(boolean mustNumber, boolean mustLetter, int maxNumber, int minNumber) {
        this.mustNumber = mustNumber;
        this.mustLetter = mustLetter;
        this.maxNumber = maxNumber;
        this.minNumber = minNumber;
    }

    public void setContent(String text) {
        contentView.setText(text);
    }

    public void setContentV2(String text) {
        contentViewV2.setVisibility(View.VISIBLE);
        contentViewV2.setText(text);
    }

    public void setContentGravity(int gravity) {
        contentView.setGravity(gravity);
    }

    public void setTitle(String text) {
        title.setText(text);
    }

}

