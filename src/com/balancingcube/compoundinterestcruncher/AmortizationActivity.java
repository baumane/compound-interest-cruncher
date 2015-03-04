/** Compound Interest Cruncher
    Copyright (C) 2013-2015  Erick Bauman

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.balancingcube.compoundinterestcruncher;

import java.text.DecimalFormat;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class AmortizationActivity extends Activity {
	public static double pv;
	public static double i;
	public static double n;
	public static double pmt;
	public static double pyr;
	
	private ProgressBar mProgress;
	private TableLayout table;
	
	private Handler mHandler = new Handler();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.tableprog);//Start with the progress bar.
        mProgress = (ProgressBar) findViewById(R.id.progressBar1);
        
        new Thread(new Runnable(){

			public void run() {
        
		        table = new TableLayout(AmortizationActivity.this);
		        
		        TableRow row = new TableRow(AmortizationActivity.this);
		        TextView column = new TextView(AmortizationActivity.this);
		        column.setText("Per.");
		        column.setBackgroundColor(Color.DKGRAY);
		        row.addView(column);
		        column = new TextView(AmortizationActivity.this);
		        column.setText("Beg. Bal.");
		        column.setBackgroundColor(Color.GRAY);
		        row.addView(column);
		        column = new TextView(AmortizationActivity.this);
		        column.setText("Int.");
		        column.setBackgroundColor(Color.DKGRAY);
		        row.addView(column);
		        column = new TextView(AmortizationActivity.this);
		        column.setText("Prin. Red.");
		        column.setBackgroundColor(Color.GRAY);
		        row.addView(column);
		        column = new TextView(AmortizationActivity.this);
		        column.setText("End Bal.");
		        column.setBackgroundColor(Color.DKGRAY);
		        row.addView(column);
		        column = new TextView(AmortizationActivity.this);
		        column.setText("Cum. Int.");
		        column.setBackgroundColor(Color.GRAY);
		        row.addView(column);
		        
		        table.addView(row);
		        
		        double cumInt = 0;//Cumulative interest
		        double actualPV = pv;//Use this to continue to get correct results despite rotating phone
		        DecimalFormat df = new DecimalFormat("#.##");//Allow me to display only 2 decimal points
		        
		        for( int k = 0; k < n; k++)
		        {
		        	row = new TableRow(AmortizationActivity.this);
		        	double temp = -1;
		        	
		        	column = new TextView(AmortizationActivity.this);
		        	column.setText(""+(k+1));//Period num.
		        	column.setBackgroundColor(Color.GRAY);
		        	row.addView(column);
		        	
		        	column = new TextView(AmortizationActivity.this);
		        	column.setText(""+df.format(pv));//Beginning balance
		        	column.setTextColor(Color.BLACK);
		        	column.setBackgroundColor(Color.LTGRAY);
		        	row.addView(column);
		        	
		        	column = new TextView(AmortizationActivity.this);
		        	temp = pv*(i);//Interest (we don't have to divide by pyr because we already did)
		        	column.setText(""+df.format(temp));
		        	column.setBackgroundColor(Color.GRAY);
		        	row.addView(column);
		        	
		        	cumInt += temp;
		        	
		        	column = new TextView(AmortizationActivity.this);
		        	temp = pmt-temp;//Principal Red.
		        	column.setText(""+df.format(temp));
		        	column.setTextColor(Color.BLACK);
		        	column.setBackgroundColor(Color.LTGRAY);
		        	row.addView(column);
		        	
		        	column = new TextView(AmortizationActivity.this);
		        	temp = pv-temp;//Ending balance
		        	column.setText(""+df.format(temp));
		        	column.setBackgroundColor(Color.GRAY);
		        	row.addView(column);
		        	
		        	column = new TextView(AmortizationActivity.this);
		        	column.setText(""+df.format(cumInt));//Cumulative interest
		        	column.setTextColor(Color.BLACK);
		        	column.setBackgroundColor(Color.LTGRAY);
		        	row.addView(column);
		        	
		        	pv = temp;//Set new beginning balance to old ending balance
		        	
		        	table.addView(row);
		        }
		        
		        pv = actualPV;
		        
		        final ScrollView scroller = new ScrollView(AmortizationActivity.this);
		        scroller.addView(table);
		        
		        mHandler.post(new Runnable(){

					public void run() {
						setContentView(scroller);//Replace the progress circle with the table.  Do this in the UI thread.
					}
		        	
		        });
		        
			}
        	
        }).start();
        
        //this.setContentView(R.layout.main);
    }
}
