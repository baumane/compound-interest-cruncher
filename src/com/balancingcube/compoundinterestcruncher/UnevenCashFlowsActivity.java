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


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;

public class UnevenCashFlowsActivity extends Activity {
	EditText pv;
	EditText i;
	EditText n;
	EditText fv;
	EditText pyr;
	double pvd;
	double id;
	double nd;
	double fvd;
	double pyrd;
	String pvs;
	String is;
	String ns;
	String fvs;
	String pyrs;
	Context context;
	
	EditText pmt;
	RadioButton ord;//Ordinary annuity
	RadioButton due;//Annuity due
	
	LinearLayout flowAmount;
    LinearLayout flowPeriod;
    LinearLayout flowRemove;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cashflows);
        context = this;
        flowAmount = (LinearLayout)findViewById(R.id.flow_amount);
        flowPeriod = (LinearLayout)findViewById(R.id.flow_period);
        flowRemove = (LinearLayout)findViewById(R.id.flow_remove);
        
        /**
         * For now, just remove the initial fields there and make them only accessible via flowAdd
         */
        flowAmount.removeViewAt(1);
        flowPeriod.removeViewAt(1);
        flowRemove.removeViewAt(1);
        
        
        
        Button solve = (Button) findViewById(R.id.solver);
        Button flowAdd = (Button) findViewById(R.id.flowadder);
        
        pv = (EditText) findViewById(R.id.editText1);
        i = (EditText) findViewById(R.id.editText2);
        pyr = (EditText) findViewById(R.id.editText3);
        
        flowAdd.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		EditText amount = new EditText(context);
        		amount.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED|InputType.TYPE_NUMBER_FLAG_DECIMAL);//Allow for negative amounts and fractional amounts
        		EditText period = new EditText(context);
        		period.setInputType(InputType.TYPE_CLASS_NUMBER);
        		final Button remove = new Button(context);
        		remove.setText("X");
        		//Remove these three fields on click
        		remove.setOnClickListener(new View.OnClickListener() {
                	public void onClick(View view) {
                		int indexToRemove = ((LinearLayout)remove.getParent()).indexOfChild(remove);
                		flowAmount.removeViewAt(indexToRemove);
                		flowPeriod.removeViewAt(indexToRemove);
                		flowRemove.removeViewAt(indexToRemove);
                	}
        		});
        		flowAmount.addView(amount);
        		flowPeriod.addView(period);
        		flowRemove.addView(remove);
        	}
        });
        
        solve.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		try{
	        		pvs = pv.getText().toString();
	    			is = i.getText().toString();
	    			pyrs = pyr.getText().toString();
	    			
	    			//Payments per year calculation (or is it periods per year?)
	    			if( pyrs.equals("") )
	    				pyrd = 1;
	    			else
	    				pyrd = Double.parseDouble(pyrs);
	    			
	    			double[] flowAmounts = new double[flowAmount.getChildCount()-1];//-1 because one of the children is the header
	    			for( int i = 0; i < flowAmounts.length; i++ )
	    			{
	    				flowAmounts[i] = Double.parseDouble(((EditText)flowAmount.getChildAt(i+1)).getText().toString());
	    			}
	    			double[] flowPeriods = new double[flowPeriod.getChildCount()-1];//-1 because one of the children is the header
	    			for( int i = 0; i < flowPeriods.length; i++ )
	    			{
	    				flowPeriods[i] = Double.parseDouble(((EditText)flowPeriod.getChildAt(i+1)).getText().toString());
	    			}	
	    			
	    			String title = "";
	    			String result = "";
	    			
	    			if( pvs.equals("") )
	    			{
	    				title = "Present Value";
	    				id = Double.parseDouble(is)/pyrd;
	    				result = ""+findPVOfFlows(flowAmounts, flowPeriods, id);
	    			}
	    			else if( is.equals("") )
	    			{
	    				title = "IRR";
	    				pvd = Double.parseDouble(pvs);
	    				double irrGuess = .5;
	    				double step = 0.5;//Initial alteration step is .5
	    				double error = Double.NaN;
	    				double prevError = Double.NaN;
	    				double approx = findPVOfFlows(flowAmounts, flowPeriods, irrGuess);
	    				int steps = 0;
	    				boolean adding = true;
	    				do{
	    					if( error == Double.NaN )
	    					{
	    						error = pvd-approx;
	    					}else
	    					{
	    						prevError = error;
	    						error = pvd-approx;
	    					}
	    					
	    					if( error == 0 )//If, by some miracle, the error is gone, exit the loop.  We found i.
	    						break;
	    					
	    					if( (error > 0 && prevError < 0) || (error < 0 && prevError > 0 ) )//If the sign changed, half the step (if we passed over the number, decrease step amount)
	    					{
	    						step/=2;
	    					}
	    					
	    					if( Math.abs(error) > Math.abs(prevError) )	//If the new error is larger than the old one
	    						adding = !adding;//Go the other direction
	    						
	    					if( adding )
	    						irrGuess+=step;
	    					else
	    						irrGuess-=step;
	    					
	    					approx = findPVOfFlows(flowAmounts, flowPeriods, irrGuess);
	    					
	    					//System.out.println("guess "+irrGuess+ " approx "+approx);
	    					
	    					steps++;
	    					
	    					if( steps > 1000000000 )//If this takes over 1 billion steps, we have failed...
	    					{
	    						result = "Calculation timed out at: ";
	    						break;
	    					}
	    					
	    				}while( Math.abs(pvd-approx) > CompoundCruncher.error_threshold );
	    				
	    				result = ""+irrGuess;
	    			}
	    			else
	    			{
	    				title = "You didn't leave one blank!";
	    				result = "Follow the instructions.";
	    			}
	    			new AlertDialog.Builder(context).setTitle(title).setMessage(result).setPositiveButton("OK", null).create().show();
        		}catch(Exception e)
        		{
        			e.printStackTrace();
        			new AlertDialog.Builder(context).setTitle("Error").setMessage("Something went wrong:"+e).setPositiveButton("OK", null).create().show();
        		}
        	}
        });
        	   
    }
    
    public double findPVOfFlows(double[] flowAmounts, double[] flowPeriods, double id)
    {
    	double pvSum = 0;
		for( int i = 0; i < flowAmounts.length; i++ )
		{
			nd = flowPeriods[i];
			fvd = flowAmounts[i];
			pvSum += CompoundCruncher.findPV(id, nd, fvd);
			System.out.println("pvSum "+pvSum);
		}
		return pvSum;
    }
}