package cn.cz.tetris;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import cn.cz.tetris.music.MusicService;
import cn.cz.tetris.utils.SPUtils;
import cn.cz.tetris.utils.Utils;
import cn.cz.tetris.view.ViewBox;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SettingActivity";

    private ViewBox mViewBox;
    private TextView mLevelText;
    private TextView mSpeedText;
    private TextView mMusicText;

    private String[] mLevelStrings;
    private String[] mSpeedStrings;
    private String[] mMusicStrings;
    private int[] mSpeeds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mViewBox = new ViewBox(this);
        mLevelStrings = Utils.getLevelStrings(this);
        mSpeedStrings = Utils.getSpeedStrings(this);
        mMusicStrings = Utils.getMusicStrings(this);
        mSpeeds = Utils.getSpeeds();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.setting_title);
        findViewById(R.id.level_setting).setOnClickListener(this);
        findViewById(R.id.speed_setting).setOnClickListener(this);
        findViewById(R.id.music_setting).setOnClickListener(this);
        mLevelText = findViewById(R.id.level);
        mSpeedText = findViewById(R.id.speed);
        mMusicText = findViewById(R.id.music);
        mLevelText.setText(mLevelStrings[SPUtils.getLevel(this)]);
        mSpeedText.setText(mSpeedStrings[Utils.getSpeedIndex(SPUtils.getSpeed(this), mSpeeds)]);
        mMusicText.setText(mMusicStrings[SPUtils.getMusic(this)]);

        TextView maxScoreText = findViewById(R.id.max_score);
        maxScoreText.setText(String.valueOf(SPUtils.getMaxScore(this)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.level_setting:
                mViewBox.showRadioDialog(getString(R.string.level_setting), new ViewBox.IRadioInput() {
                    @Override
                    public void onRadioInput(String name) {
                        for (int i = 0; i != mLevelStrings.length; i++) {
                            if (name.equals(mLevelStrings[i])) {
                                SPUtils.setLevel(SettingActivity.this, i);
                                mLevelText.setText(name);
                                break;
                            }
                        }
                        setResult(RESULT_OK);
                    }
                }, SPUtils.getLevel(this), mLevelStrings);
                break;
            case R.id.speed_setting:
                mViewBox.showRadioDialog(getString(R.string.speed_setting), new ViewBox.IRadioInput() {
                    @Override
                    public void onRadioInput(String name) {
                        for (int i = 0; i != mSpeedStrings.length; i++) {
                            if (name.equals(mSpeedStrings[i])) {
                                SPUtils.setSpeed(SettingActivity.this, mSpeeds[i]);
                                mSpeedText.setText(name);
                                break;
                            }
                        }
                        setResult(RESULT_OK);
                    }
                }, Utils.getSpeedIndex(SPUtils.getSpeed(this), mSpeeds), mSpeedStrings);
                break;
            case R.id.music_setting:
                mViewBox.showRadioDialog(getString(R.string.music_setting), new ViewBox.IRadioInput() {
                    @Override
                    public void onRadioInput(String name) {
                        for (int i = 0; i != mMusicStrings.length; i++) {
                            if (name.equals(mMusicStrings[i])) {
                                SPUtils.setMusic(SettingActivity.this, i);
                                mMusicText.setText(name);
                                break;
                            }
                        }
                        Intent intent = new Intent();
                        intent.putExtra(MusicService.STR_MUSIC_ID, SPUtils.getMusic(SettingActivity.this));
                        setResult(RESULT_OK, intent);
                    }
                }, SPUtils.getMusic(this), mMusicStrings);
                break;
        }
    }
}
