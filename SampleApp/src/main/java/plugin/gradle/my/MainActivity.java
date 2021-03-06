package plugin.gradle.my;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.shit.testlibrary.TestLibraryClass;

import dt.monitor.injected.InteractiveInjected;
import plugin.gradle.my.concurrent_test.ExecutorManager;
import plugin.gradle.my.dummy.ScrollingActivity;

public class MainActivity extends AppCompatActivity {

    private TestLibraryClass test;

    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET};
    private AlertDialog dialog;

//    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        gogo(100);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setClickable(true);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.i("zkw", "onTouch java");
                }
                return false;
            }
        });

        Fragment f = new Fragment();
        android.app.Fragment ff = new android.app.Fragment();

        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission();
            }
        }

    }

    // 提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission() {

        new AlertDialog.Builder(this).setTitle("存储权限不可用").setMessage("由于需要获取存储空间，为你存储个人信息；\n否则，您将无法正常使用支付宝")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setCancelable(false).show();
    }

    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else {
                        finish();
                    }
                } else {
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 提示用户去应用设置界面手动开启权限

    private void showDialogTipUserGoToAppSettting() {

        dialog = new AlertDialog.Builder(this).setTitle("存储权限不可用").setMessage("请在-应用设置-权限-中，允许使用存储权限来保存用户数据")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, 123);
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(this, permissions[0]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.i("zkw", "----------------_>>>on trim memory:" + level);
    }

    public void onInteractiveTest(View view) {
        Intent i = new Intent(this, InteractiveTestActivity.class);
        startActivity(i);
    }

    public void onGoClick(View view) {
        Intent i = new Intent(this, ScrollingActivity.class);
        startActivity(i);

        TabLayout.OnTabSelectedListener listener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i(tab.getText().toString(), "");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.i(tab.getText().toString(), "");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.i(tab.getText().toString(), "");
            }
        };
    }

    public void onSlowClick(View view) {
//        Intent i = new Intent(this, SecondActivity.class);
//        startActivity(i);
        DroidTelescopeProxy.startMethodTracing();
//        TimeConsumingSample.methodEnter("plugin.gradle.my.MainActivity", "onSlowClick", "android.view.View");
        try {
            g2();
        } catch (IllegalAccessException e) {
//            e.printStackTrace();
        }
        gogo(87878787);

        if (test == null) {
            test = new TestLibraryClass();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                isT("");
                try {
                    g2();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                test.startTestt();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        test.toding();

//        new TestLibraryClass().startTestt();
        stopTracing();

    }

    private void stopTracing() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        TimeConsumingSample.methodExit(System.nanoTime() - 100000, SystemClock.currentThreadTimeMillis() - 100, "plugin.gradle.my.MainActivity", "stopTracing", "");
//        TimeConsumingSample.methodExitFinally("plugin.gradle.my.MainActivity", "stopTracing", "");
        stopTracing("nnn");
    }

    private void stopTracing(String string) {
        String path = DroidTelescopeProxy.stopMethodTracing(this);
        if (!TextUtils.isEmpty(path)) {
            Log.i("zkw", "加载完成。。。。。。。。:::>" + path);
        } else {
            Log.i("zkw", "path is null!!!!!!");
        }
    }

    public int gogo(int c) {
        int a = Log.i("zkw", "hello");
        if (a > 0) {
            Log.i("zkw", ">>0");
        } else {
            Log.i("zkw", "<<0");
        }
        isT(new Paint());
        isT(this);
        return a;
    }

    public Object g2() throws IllegalAccessException {
        if (isT("")) {
            Log.i("g2", "isT() true");
            throw new IllegalAccessException();
        }
        try {
            throw new NullPointerException();
        } catch (NullPointerException e) {
            Log.i("g2", "NullPointerException!!!!");
        } finally {
            Log.i("g2", "finally");
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isT(Object o) {
        Log.i("", "=======" + o);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean stoped = false;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (stoped) {
            return;
        } else {
            stop(null);
            stoped = true;
        }

    }

    private void stop(TestLibraryClass c) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String path = DroidTelescopeProxy.stopMethodTracing(this.getApplicationContext());
        if (!TextUtils.isEmpty(path)) {
            Log.i("zkw", "加载完成。。。。。。。。:::>" + path);
        } else {
            Log.i("zkw", "path is null!!!!!!");
        }
    }

    private void empty1() {
    }

    private void empty2(String s) {
    }

    private boolean empty3(String s) {
        return true;
    }

    private String empty4(String s) {
        return s;
    }

    private String a = "sss";

    private String emptyGet() {
        return a;
    }

    private void emptySet(String m) {
        a = m;
    }

}
