package com.fedexday.trump.trump;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class MainActivity extends AppCompatActivity implements TrumpMVP.View, TextToSpeech.OnInitListener {


    private static final String TAG = "View";
    private static final int REQ_CODE_SPEECH_INPUT = 1;
    TrumpMVP.Presenter trumpPresenter;
    TrumpRetrofitService trumpRetrofitService;
    TextToSpeech tts;
    EditText voiceRecognitionEditText;

    private FabMoveAnimation fabMoveAnimation;

    private List<FeedItem> feedsList = new ArrayList<>();
    private TrumpRecyclerViewAdapter trumpRecyclerViewAdapter;


    @BindView(R.id.main_user_edit_text)
    EditText mainUserEditTest;

    @BindView(R.id.main_bot_recycler)
    RecyclerView mainBotRecycler;

    @BindView(R.id.fab)
    ImageButton fab;

    @BindView(R.id.keyboard_option)
    ImageButton keyboardButton;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        voiceRecognitionEditText = new EditText(this);

        fabMoveAnimation = new FabMoveAnimation(fab);

        fab.setOnClickListener(view -> {
            promptSpeechInput();
        });

        trumpRetrofitService = initRetrofit().create(TrumpRetrofitService.class);
        trumpPresenter = new TrumpPresenterImpl();
        trumpPresenter.setView(this);
        trumpPresenter.setRetrofitService(trumpRetrofitService);
        trumpPresenter.listenVoice(getMainUserObservable(mainUserEditTest));

        tts = new TextToSpeech(this, this);
        tts.setLanguage(Locale.US);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mainBotRecycler.setLayoutManager(linearLayoutManager);
        trumpRecyclerViewAdapter = new TrumpRecyclerViewAdapter(this,feedsList);
        mainBotRecycler.setAdapter(trumpRecyclerViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    Observable<CharSequence> getEditTextObservable(final EditText editText) {
        return
                RxTextView
                        .textChanges(editText)
                        .skip(1).filter(charSequence -> !(charSequence.length() == 0))
                        .debounce(2000, TimeUnit.MILLISECONDS);
    }

    @Override
    public Observable getMainUserObservable(EditText editText) {
        return Observable.merge(getEditTextObservable(editText),getVoiceRecognitionObservable());
    }

    private Observable getVoiceRecognitionObservable() {
        return
                RxTextView
                        .textChanges(voiceRecognitionEditText)
                        .filter(charSequence -> !(charSequence.length() == 0))
                        ;
    }

    @Override
    public void appendToRecyclerView(String line , int side) {
        Log.d(TAG,line);
        feedsList.add(new FeedItem(line,side));
        trumpRecyclerViewAdapter.notifyDataSetChanged();
        mainBotRecycler.smoothScrollToPosition(trumpRecyclerViewAdapter.getItemCount()-1);
    }

    @Override
    public void ttsSpeak(String line) {
        String utteranceId=(line.equals("bye bye")) ? "bye bye" : this.hashCode() + "" ;
        tts.speak(line, TextToSpeech.QUEUE_FLUSH, null,utteranceId);
    }

    @Override
    public void progressBarVisability(boolean visible) {
        progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void exit() {
        finish();
    }

    @Override
    public void resetEditText() {
//        runOnUiThread(() -> {
        String manualString = mainUserEditTest.getText().toString();
        String voiceString = voiceRecognitionEditText.getText().toString();

        if (manualString.isEmpty())
            appendToRecyclerView(voiceRecognitionEditText.getText().toString(),FeedItem.USER);
        else
            appendToRecyclerView(mainUserEditTest.getText().toString(),FeedItem.USER);

        mainUserEditTest.setText("");
        voiceRecognitionEditText.setText("");
//        });
    }

    private Retrofit initRetrofit(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://finbotapp.apphb.com/")
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.keyboard_option)
    void keyboardButton(){
        if (mainUserEditTest.getVisibility() == View.GONE){
            fabMoveAnimation.moveAway();
            mainUserEditTest.setVisibility(View.VISIBLE);

        } else {
            fabMoveAnimation.moveBack();
            mainUserEditTest.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mainUserEditTest.getWindowToken(), 0);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    voiceRecognitionEditText.setText(result.get(0));
                    fabMoveAnimation.moveBack();
                }
                break;
            }

        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            if (status == TextToSpeech.SUCCESS) {
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if (utteranceId.equals("bye bye"))
                            exit();
                        Observable.timer(500,TimeUnit.MILLISECONDS)
                                .subscribe(aLong -> promptSpeechInput());
                    }

                    @Override
                    public void onError(String utteranceId) {
                    }
                });
            }
        }
    }


}
