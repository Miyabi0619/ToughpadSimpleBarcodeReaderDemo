package com.example.zebra.toughpadsimplebarcodereaderdemo;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.panasonic.toughpad.android.api.barcode.BarcodeData;
import com.panasonic.toughpad.android.api.serial.SerialPort;
import com.panasonic.toughpad.android.api.serial.SerialPortManager;
import com.panasonic.toughpad.android.api.cradle.Cradle;

import java.util.List;

public class MainActivity extends BarcodeReadableActivity {
  private TextView textView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        MainActivity.this.switchSoftwareTrigger(true);
      }
    });

    textView = (TextView) findViewById(R.id.text_view);
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

    if (id == R.id.action_device_info) {
      showDeviceInfo();
      return true;
    }

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void showDeviceInfo() {
    StringBuilder info = new StringBuilder();

    // シリアルポート情報
    info.append("=== Serial Ports ===\n");
    try {
      List<SerialPort> ports = SerialPortManager.getSerialPorts();
      if (ports != null && !ports.isEmpty()) {
        for (SerialPort port : ports) {
          info.append("- ").append(port.getDeviceName());
          info.append(" (enabled: ").append(port.isEnabled()).append(")\n");
        }
      } else {
        info.append("No serial ports found\n");
      }
    } catch (Exception e) {
      info.append("Error: ").append(e.getMessage()).append("\n");
    }

    // クレードル情報
    info.append("\n=== Cradle ===\n");
    try {
      int cradleType = Cradle.getCradleType();
      if (cradleType != Cradle.CRADLE_TYPE_NONE) {
        info.append("Cradle detected\n");
        String typeName;
        switch (cradleType) {
          case Cradle.CRADLE_TYPE_NORMAL:
            typeName = "NORMAL";
            break;
          case Cradle.CRADLE_TYPE_COMMUNICATION:
            typeName = "COMMUNICATION";
            break;
          case Cradle.CRADLE_TYPE_ERROR:
            typeName = "ERROR";
            break;
          default:
            typeName = "UNKNOWN";
            break;
        }
        info.append("Type: ").append(typeName).append("\n");
      } else {
        info.append("No cradle detected\n");
      }
    } catch (Exception e) {
      info.append("Error: ").append(e.getMessage()).append("\n");
    }

    // ダイアログで表示
    new AlertDialog.Builder(this)
        .setTitle("Toughpad Device Info")
        .setMessage(info.toString())
        .setPositiveButton("OK", null)
        .show();
  }

  @Override
  public void onRead(BarcodeData barcodeData) {
    appendResult(barcodeData.getTextData());
  }

  private void appendResult(final String result) {
    if (getText(R.string.hint_text).equals(textView.getText().toString())) {
      textView.setText("");
    }

    textView.append("\n" + result);
  }
}
