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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class CompoundInterestCruncherActivity extends Activity {
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
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        context = this;
        
        Button solve = (Button) findViewById(R.id.solver);
        	
        pv = (EditText) findViewById(R.id.editText1);
        i = (EditText) findViewById(R.id.editText2);
        n = (EditText) findViewById(R.id.editText3);
        fv = (EditText) findViewById(R.id.editText4);
        pyr = (EditText) findViewById(R.id.editText5);
        
        pmt = (EditText) findViewById(R.id.editText6);
        ord = (RadioButton) findViewById(R.id.radio0);
        due = (RadioButton) findViewById(R.id.radio1);
        
        Button solveNoPresent = (Button) findViewById(R.id.button2);//Solve, using FV equation
        Button solveNoFuture = (Button) findViewById(R.id.button3);//Solve, using PV equation
        
        Button amoTable = (Button) findViewById(R.id.button4);
        Button solveBond = (Button) findViewById(R.id.button5);
        
        Button unevenFlows = (Button) findViewById(R.id.button6);
        		
        solve.setOnClickListener(new View.OnClickListener() {
                	public void onClick(View view) {
                		try{
                			pvs = pv.getText().toString();
                			is = i.getText().toString();
                			ns = n.getText().toString();
                			fvs = fv.getText().toString();
                			pyrs = pyr.getText().toString();
                			
                			//Payments per year calculation (or is it periods per year?)
                			if( pyrs.equals("") )
                				pyrd = 1;
                			else
                				pyrd = Double.parseDouble(pyrs);
                			
                			String title = "";
                			String result = "";
                			
                			if( pvs.equals("") )
                			{
                				title = "Present Value";
                				id = Double.parseDouble(is)/pyrd;
                				nd = Double.parseDouble(ns);
                				fvd = Double.parseDouble(fvs);
                				result = ""+CompoundCruncher.findPV(id, nd, fvd);
                			}
                			else if( is.equals("") )
                			{
                				title = "Interest Rate/Year";
                				pvd = Double.parseDouble(pvs);
                				nd = Double.parseDouble(ns);
                				fvd = Double.parseDouble(fvs);
                				result = ""+CompoundCruncher.findI(pvd, nd, fvd)*pyrd;//TODO: Make sure this is how to treat payment periods
                			}
                			else if( ns.equals("") )
                			{
                				title = "Number of Periods";
                				pvd = Double.parseDouble(pvs);
                				id = Double.parseDouble(is)/pyrd;
                				fvd = Double.parseDouble(fvs);
                				result = ""+CompoundCruncher.findN(pvd, id, fvd);
                			}
                			else if( fvs.equals("") )
                			{
                				title = "Future Value";
                				pvd = Double.parseDouble(pvs);
                				id = Double.parseDouble(is)/pyrd;
                				nd = Double.parseDouble(ns);
                				result = ""+CompoundCruncher.findFV(id, nd, pvd);
                			}
                			else
                			{
                				title = "You didn't leave one blank!";
                				result = "Follow the instructions.";
                			}
                			new AlertDialog.Builder(context).setTitle(title).setMessage(result).setPositiveButton("OK", null).create().show();
                		}catch(Exception e)
                		{
                			new AlertDialog.Builder(context).setTitle("Error").setMessage("Something went wrong.").setPositiveButton("OK", null).create().show();
                		}
                	}
        });
        
        solveNoPresent.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		try{
        			is = i.getText().toString();
        			ns = n.getText().toString();
        			fvs = fv.getText().toString();
        			pyrs = pyr.getText().toString();
        			
        			String pmts = pmt.getText().toString();
        			double pmtd;
        			
        			//Payments per year calculation (or is it periods per year?)
        			if( pyrs.equals("") )
        				pyrd = 1;
        			else
        				pyrd = Double.parseDouble(pyrs);
        			
        			String title = "";
        			String result = "";
        			
        			if( pmts.equals("") )
        			{
        				title = "Payment Amount per Period";
        				id = Double.parseDouble(is)/pyrd;
        				nd = Double.parseDouble(ns);
        				fvd = Double.parseDouble(fvs);
        				result = ""+CompoundCruncher.findPMTAnnFV(id, nd, fvd, due.isChecked());
        			}
        			else if( is.equals("") )
        			{
        				title = "Interest Rate/Year";
        				pmtd = Double.parseDouble(pmts);
        				nd = Double.parseDouble(ns);
        				fvd = Double.parseDouble(fvs);
        				result = ""+CompoundCruncher.findIAnnFV(nd, pmtd, fvd, due.isChecked())*pyrd;//TODO: Make sure this is how to treat payment periods
        			}
        			else if( ns.equals("") )
        			{
        				title = "Number of Periods";
        				pmtd = Double.parseDouble(pmts);
        				id = Double.parseDouble(is)/pyrd;
        				fvd = Double.parseDouble(fvs);
        				result = ""+CompoundCruncher.findNAnnFV(id, pmtd, fvd, due.isChecked());
        			}
        			else if( fvs.equals("") )
        			{
        				title = "Future Value";
        				pmtd = Double.parseDouble(pmts);
        				id = Double.parseDouble(is)/pyrd;
        				nd = Double.parseDouble(ns);
        				result = ""+CompoundCruncher.findFVAnnFV(id, nd, pmtd, due.isChecked());
        			}
        			else
        			{
        				title = "You didn't leave one blank!";
        				result = "Follow the instructions.";
        			}
        			new AlertDialog.Builder(context).setTitle(title).setMessage(result).setPositiveButton("OK", null).create().show();
        		}catch(Exception e)
        		{
        			new AlertDialog.Builder(context).setTitle("Error").setMessage("Something went wrong.").setPositiveButton("OK", null).create().show();
        		}
        	}
        });
        
        solveNoFuture.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		try{
        			pvs = pv.getText().toString();
        			is = i.getText().toString();
        			ns = n.getText().toString();
        			pyrs = pyr.getText().toString();
        			
        			String pmts = pmt.getText().toString();
        			double pmtd;
        			
        			//Payments per year calculation (or is it periods per year?)
        			if( pyrs.equals("") )
        				pyrd = 1;
        			else
        				pyrd = Double.parseDouble(pyrs);
        			
        			String title = "";
        			String result = "";
        			
        			if( pmts.equals("") )
        			{
        				title = "Payment Amount per Period";
        				id = Double.parseDouble(is)/pyrd;
        				nd = Double.parseDouble(ns);
        				pvd = Double.parseDouble(pvs);
        				result = ""+CompoundCruncher.findPMTAnnPV(id, nd, pvd, due.isChecked());
        			}
        			else if( pvs.equals("") )
        			{
        				title = "Present Value";
        				id = Double.parseDouble(is)/pyrd;
        				nd = Double.parseDouble(ns);
        				pmtd = Double.parseDouble(pmts);
        				//fvd = Double.parseDouble(fvs);
        				result = ""+CompoundCruncher.findPVAnnPV(id, nd, pmtd, due.isChecked());
        			}
        			else if( is.equals("") )
        			{
        				title = "Interest Rate/Year";
        				pvd = Double.parseDouble(pvs);
        				nd = Double.parseDouble(ns);
        				pmtd = Double.parseDouble(pmts);
        				//fvd = Double.parseDouble(fvs);
        				result = ""+CompoundCruncher.findIAnnPV(nd, pmtd, pvd, due.isChecked())*pyrd;//TODO: Make sure this is how to treat payment periods
        			}
        			else if( ns.equals("") )
        			{
        				title = "Number of Periods";
        				pvd = Double.parseDouble(pvs);
        				id = Double.parseDouble(is)/pyrd;
        				pmtd = Double.parseDouble(pmts);
        				//fvd = Double.parseDouble(fvs);
        				result = ""+CompoundCruncher.findNAnnPV(id, pmtd, pvd, due.isChecked());
        			}
        			else
        			{
        				title = "You didn't leave one blank!";
        				result = "Follow the instructions.";
        			}
        			new AlertDialog.Builder(context).setTitle(title).setMessage(result).setPositiveButton("OK", null).create().show();
        		}catch(Exception e)
        		{
        			new AlertDialog.Builder(context).setTitle("Error").setMessage("Something went wrong.").setPositiveButton("OK", null).create().show();
        		}
        	}
        });
        
        amoTable.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		
        		try{
        			pvs = pv.getText().toString();
        			is = i.getText().toString();
        			ns = n.getText().toString();
        			pyrs = pyr.getText().toString();
        			
        			String pmts = pmt.getText().toString();
        			double pmtd;
        			
        			//Payments per year calculation (or is it periods per year?)
        			if( pyrs.equals("") )
        				pyrd = 1;
        			else
        				pyrd = Double.parseDouble(pyrs);
        			
        			String title = "";
        			String result = "";
        			
        			if( pmts.equals("") )
        			{
        				title = "Payment Amount per Period";
        				id = Double.parseDouble(is)/pyrd;
        				nd = Double.parseDouble(ns);
        				pvd = Double.parseDouble(pvs);
        				pmtd = CompoundCruncher.findPMTAnnPV(id, nd, pvd, due.isChecked());
        			}
        			else if( pvs.equals("") )
        			{
        				title = "Present Value";
        				id = Double.parseDouble(is)/pyrd;
        				nd = Double.parseDouble(ns);
        				pmtd = Double.parseDouble(pmts);
        				//fvd = Double.parseDouble(fvs);
        				pvd = CompoundCruncher.findPVAnnPV(id, nd, pmtd, due.isChecked());
        			}
        			else if( is.equals("") )
        			{
        				title = "Interest Rate/Year";
        				pvd = Double.parseDouble(pvs);
        				nd = Double.parseDouble(ns);
        				pmtd = Double.parseDouble(pmts);
        				//fvd = Double.parseDouble(fvs);
        				id = CompoundCruncher.findIAnnPV(nd, pmtd, pvd, due.isChecked())*pyrd;//TODO: Make sure this is how to treat payment periods
        			}
        			else if( ns.equals("") )
        			{
        				title = "Number of Periods";
        				pvd = Double.parseDouble(pvs);
        				id = Double.parseDouble(is)/pyrd;
        				pmtd = Double.parseDouble(pmts);
        				//fvd = Double.parseDouble(fvs);
        				nd = CompoundCruncher.findNAnnPV(id, pmtd, pvd, due.isChecked());
        			}
        			else
        			{
        				title = "You didn't leave one blank!";
        				result = "Follow the instructions.";
        				new AlertDialog.Builder(context).setTitle(title).setMessage(result).setPositiveButton("OK", null).create().show();
        				return;//Failed to enter data correctly, but did not do so poorly that it throws an exception
        			}
        			//Here we assume we succeeded in obtaining the numbers
        			AmortizationActivity.i = id;
        			AmortizationActivity.n = nd;
        			AmortizationActivity.pmt = pmtd;
        			AmortizationActivity.pv = pvd;
        			AmortizationActivity.pyr = pyrd;
        			
        			Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.balancingcube.compoundinterestcruncher", "com.balancingcube.compoundinterestcruncher.AmortizationActivity"));
                    startActivity( intent );
        		}catch(Exception e)
        		{
        			new AlertDialog.Builder(context).setTitle("Error").setMessage("Something went wrong.").setPositiveButton("OK", null).create().show();
        		}
        	}
        });
        
        /*
         * For now, this will only solve for present value (easy),
         * i (harder), n (harder), and pmt (harder).
         */
        solveBond.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		try{
        			pvs = pv.getText().toString();
        			is = i.getText().toString();
        			ns = n.getText().toString();
        			pyrs = pyr.getText().toString();
        			
        			String pmts = pmt.getText().toString();
        			double pmtd;
        			
        			//Payments per year calculation (or is it periods per year?)
        			if( pyrs.equals("") )
        				pyrd = 1;
        			else
        				pyrd = Double.parseDouble(pyrs);
        			
        			String title = "";
        			String result = "";
        			
        			if( pmts.equals("") )
        			{
        				title = "Payment Amount per Period";
        				id = Double.parseDouble(is)/pyrd;
        				nd = Double.parseDouble(ns);
        				pvd = Double.parseDouble(pvs);
        				result = ""+CompoundCruncher.findBondPmt(pvd, id, nd, due.isChecked());
        			}
        			else if( pvs.equals("") )
        			{
        				title = "Present Value";
        				id = Double.parseDouble(is)/pyrd;
        				nd = Double.parseDouble(ns);
        				pmtd = Double.parseDouble(pmts);
        				//fvd = Double.parseDouble(fvs);
        				result = ""+CompoundCruncher.findBondPV(id, nd, pmtd, due.isChecked());
        			}
        			else if( is.equals("") )
        			{
        				title = "Interest Rate/Year";
        				pvd = Double.parseDouble(pvs);
        				nd = Double.parseDouble(ns);
        				pmtd = Double.parseDouble(pmts);
        				//fvd = Double.parseDouble(fvs);
        				result = ""+CompoundCruncher.findBondI(pvd, nd, pmtd, due.isChecked())*pyrd;//TODO: Make sure this is how to treat payment periods
        			}
        			else if( ns.equals("") )
        			{
        				title = "Number of Periods";
        				pvd = Double.parseDouble(pvs);
        				id = Double.parseDouble(is)/pyrd;
        				pmtd = Double.parseDouble(pmts);
        				//fvd = Double.parseDouble(fvs);
        				result = ""+CompoundCruncher.findBondN(pvd, id, pmtd, due.isChecked());
        			}
        			else
        			{
        				title = "Ok, I'll admit this one's not clear.";
        				result = "FV matters not, as it will always be 1000.  Leave blank either pmt, i, n, or pv.  I am making no assumptions on P/YR, so if you are using semiannual bond payments (likely), enter 2 there.";
        			}
        			new AlertDialog.Builder(context).setTitle(title).setMessage(result).setPositiveButton("OK", null).create().show();
        		}catch(Exception e)
        		{
        			new AlertDialog.Builder(context).setTitle("Error").setMessage("Something went wrong.").setPositiveButton("OK", null).create().show();
        		}
        	}
        });
        
        unevenFlows.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.balancingcube.compoundinterestcruncher", "com.balancingcube.compoundinterestcruncher.UnevenCashFlowsActivity"));
                startActivity( intent );
        	}
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionsmenu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.aboutItem:
            	LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
            	AlertDialog.Builder build = new AlertDialog.Builder(context).setTitle("About").setPositiveButton("OK", null);
            	build.setView(inflater.inflate(R.layout.about, null));
            	build.create().show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}