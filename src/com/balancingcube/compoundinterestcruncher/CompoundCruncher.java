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

public class CompoundCruncher {
	public static double error_threshold = 0.00001;//For approximating i in annuity problems.
	
	public static double findPV(double i, double n, double fv)
	{
		return fv/Math.pow(1+i, n);
	}
	
	public static double findFV(double i, double n, double pv)
	{
		return pv*Math.pow(1+i, n);
	}
	
	public static double findN(double pv, double i, double fv)
	{
		return Math.log(fv/pv)/Math.log(1+i);
	}
	
	public static double findI(double pv, double n, double fv)
	{
		return Math.pow(fv/pv, 1/n)-1;//nth root minus 1
	}
	
	/**
	 * Find present value of an annuity using PV equation (No FV).
	 * 
	 * If due is true, it means it's an annuity due, not an ordinary annuity
	 */
	public static double findPVAnnPV(double i, double n, double pmt, boolean due)
	{
		double ani = (1-Math.pow(1+i, -n))/i;//On Wikipedia, this is present value of a payment of 1.
		if( due )
			ani *= (1+i);//This is the only modificaton needed for an annuity due
		return pmt*ani;
	}
	
	/**
	 * Find future value of an annuity using FV equation (No PV).
	 * 
	 * If due is true, it means it's an annuity due, not an ordinary annuity
	 */
	public static double findFVAnnFV(double i, double n, double pmt, boolean due)
	{
		double sni = (Math.pow(1+i, n)-1)/i;//On Wikipedia, this is future value of a payment of 1.
		if( due )
			sni *= (1+i);//This is the only modificaton needed for an annuity due
		return pmt*sni;
	}
	
	/**
	 * Find payment amount per period of an annuity using PV equation (No FV).
	 * 
	 * If due is true, it means it's an annuity due, not an ordinary annuity
	 */
	public static double findPMTAnnPV(double i, double n, double pv, boolean due)
	{
		double ani = (1-Math.pow(1+i, -n))/i;//On Wikipedia, this is present value of a payment of 1.
		if( due )
			ani *= (1+i);//This is the only modificaton needed for an annuity due
		return pv/ani;
	}
	
	/**
	 * Find payment amount per period of an annuity using FV equation (No PV).
	 * 
	 * If due is true, it means it's an annuity due, not an ordinary annuity
	 */
	public static double findPMTAnnFV(double i, double n, double fv, boolean due)
	{
		double sni = (Math.pow(1+i, n)-1)/i;//On Wikipedia, this is future value of a payment of 1.
		if( due )
			sni *= (1+i);//This is the only modificaton needed for an annuity due
		return fv/sni;
	}
	
	/**
	 * Find number of payments of an annuity using PV equation (No FV).
	 * 
	 * If due is true, it means it's an annuity due, not an ordinary annuity
	 */
	public static double findNAnnPV(double i, double pmt, double pv, boolean due)
	{
		double d = i;//For an annuity due, d becomes i/(i+1), but for an ordinary annuity, it's just i.
		if( due )
			d = i/(i+1);
		return -Math.log(1-(pv*d)/pmt)/Math.log(1+i);//According to my calculations, this = num of payments
	}
	
	/**
	 * Find number of payments of an annuity using FV equation (No PV).
	 * 
	 * If due is true, it means it's an annuity due, not an ordinary annuity
	 */
	public static double findNAnnFV(double i, double pmt, double fv, boolean due)
	{
		double d = i;//For an annuity due, d becomes i/(i+1), but for an ordinary annuity, it's just i.
		if( due )
			d = i/(i+1);
		return Math.log((fv*d)/pmt+1)/Math.log(1+i);//According to my calculations, this = num of payments
	}
	
	/**
	 * Find interest of an annuity using PV equation (No FV).
	 * 
	 * This must be approximated.
	 * 
	 * If due is true, it means it's an annuity due, not an ordinary annuity
	 */
	public static double findIAnnPV(double n, double pmt, double pv, boolean due)
	{
		double approx = 0;
		double i = 1;//Initial guess is 100% interest
		double step = 0.5;//Initial alteration step is .5
		double error = Double.NaN;
		double prevError = Double.NaN;
		do{
			if( error == Double.NaN )
			{
				error = pv-approx;
			}else
			{
				prevError = error;
				error = pv-approx;
			}
			//System.out.println("Error: "+error + " approx: "+ approx + " pv: "+pv + " i: "+i + " step: "+step);
			if( error == 0 )//If, by some miracle, the error is gone, exit the loop.  We found i.
				break;
			
			if( (error > 0 && prevError < 0) || (error < 0 && prevError > 0 ) )//If the sign changed, half the step (if we passed over the number, decrease step amount)
			{
				step/=2;
			}
			
			if( error > 0 )	//If the error is positive
				i-=step;	//adjust interest down
			else			//If the error is negative
				i+=step;	//adjust interest up
			
			if( i == 0 )
				i = 0.000000000001;//Can't divide by zero; don't let it; unfortunately, it also breaks if the number is too small
			
			double ani = (1-Math.pow(1+i, -n))/i;//On Wikipedia, this is present value of a single payment
			if( due )
				ani *= (1+i);//This is the only modificaton needed for an annuity due
			
			approx = pmt*ani;
			
			//System.out.println("Leave? "+(Math.abs(pv-approx)>error_threshold)+" Error: "+error + " approx: "+ approx + " pv: "+pv + " i: "+i + " step: "+step);
		}while(Math.abs(pv-approx)>error_threshold);
		return i;//i should now be calculated
	}
	
	/**
	 * Find interest of an annuity using FV equation (No PV).
	 * 
	 * This must be approximated.
	 * 
	 * If due is true, it means it's an annuity due, not an ordinary annuity
	 */
	public static double findIAnnFV(double n, double pmt, double fv, boolean due)
	{
		double approx = 0;
		double i = 1;//Initial guess is 100% interest
		double step = 0.5;//Initial alteration step is .5
		double error = Double.NaN;
		double prevError = Double.NaN;
		do{
			if( error == Double.NaN )
			{
				error = fv-approx;
			}else
			{
				prevError = error;
				error = fv-approx;
			}
			if( error == 0 )//If, by some miracle, the error is gone, exit the loop.  We found i.
				break;
			
			if( (error > 0 && prevError < 0) || (error < 0 && prevError > 0 ) )//If the sign changed, half the step (if we passed over the number, decrease step amount)
			{
				step/=2;
			}
			
			if( error > 0 )	//If the error is positive
				i+=step;	//adjust interest up (opposite of PV)
			else			//If the error is negative
				i-=step;	//adjust interest down (opposite of PV)
			
			if( i == 0 )
				i = 0.000000000001;//Can't divide by zero; don't let it; unfortunately, it also breaks if the number is too small
			
			double sni = (Math.pow(1+i, n)-1)/i;//On Wikipedia, this is future value of a single payment
			if( due )
				sni *= (1+i);//This is the only modificaton needed for an annuity due
			
			approx = pmt*sni;
			
			//System.out.println("Leave? "+(Math.abs(fv-approx)>error_threshold)+" Error: "+error + " approx: "+ approx + " fv: "+fv + " i: "+i + " step: "+step);
		}while(Math.abs(fv-approx)>error_threshold);
		return i;//i should now be calculated
	}
	
	/**
	 * For now, bond FV is considered to be ALWAYS $1000 (it's all I need for class).  I can change this later if I want.
	 * @param i
	 * @param n
	 * @param pmt
	 * @param due
	 * @return
	 */
	public static double findBondPV(double i, double n, double pmt, boolean due)
	{
		double annPV = findPVAnnPV(i, n, pmt, due);
		double PV = findPV(i, n, 1000);
		return annPV+PV;//Combined present values as result
	}
	
	/**
	 * For now, bond FV is considered to be ALWAYS $1000 (it's all I need for class).  I can change this later if I want.
	 * @param pv
	 * @param n
	 * @param pmt
	 * @param due
	 * @return
	 */
	public static double findBondI(double pv, double n, double pmt, boolean due)
	{
		double fv = 1000;
		double approx = 0;
		double i = 1;//Initial guess is 100% interest
		double step = 0.5;//Initial alteration step is .5
		double error = Double.NaN;
		double prevError = Double.NaN;
		do{
			if( error == Double.NaN )
			{
				error = pv-approx;
			}else
			{
				prevError = error;
				error = pv-approx;
			}
			if( error == 0 )//If, by some miracle, the error is gone, exit the loop.  We found i.
				break;
			
			if( (error > 0 && prevError < 0) || (error < 0 && prevError > 0 ) )//If the sign changed, half the step (if we passed over the number, decrease step amount)
			{
				step/=2;
			}
			
			if( error > 0 )	//If the error is positive
				i-=step;	//adjust interest
			else			//If the error is negative
				i+=step;	//adjust interest
			
			if( i == 0 )
				i = 0.000000000001;//Can't divide by zero; don't let it; unfortunately, it also breaks if the number is too small
			
			double annPV = findPVAnnPV(i, n, pmt, due);
			double finalPV = findPV(i, n, fv);
			approx = annPV+finalPV;
			//System.out.println("Leave? "+(Math.abs(pv-approx)>error_threshold)+" Error: "+error + " approx: "+ approx + " pv: "+pv + " i: "+i + " step: "+step + " annPV: "+ annPV+" finalPV: "+finalPV);
		}while(Math.abs(pv-approx)>error_threshold);
		
		return i;//i should now be calculated
	}
	
	/**
	 * For now, bond FV is considered to be ALWAYS $1000 (it's all I need for class).  I can change this later if I want.
	 * @param pv
	 * @param i
	 * @param pmt
	 * @param due
	 * @return
	 */
	public static double findBondN(double pv, double i, double pmt, boolean due)
	{
		double fv = 1000;
		double approx = 0;
		double n = 1;//Initial guess is 100% interest
		double step = 1;//Initial alteration step is 1
		double error = Double.NaN;
		double prevError = Double.NaN;
		do{
			if( error == Double.NaN )
			{
				error = pv-approx;
			}else
			{
				prevError = error;
				error = pv-approx;
			}
			if( error == 0 )//If, by some miracle, the error is gone, exit the loop.  We found i.
				break;
			
			if( (error > 0 && prevError < 0) || (error < 0 && prevError > 0 ) )//If the sign changed, half the step (if we passed over the number, decrease step amount)
			{
				step/=2;
			}
			
			if( error > 0 )	//If the error is positive
				n-=step;	//adjust period
			else			//If the error is negative
				n+=step;	//adjust period
			
			if( n == 0 )
				n = 0.000000000001;//Can't divide by zero; don't let it; unfortunately, it also breaks if the number is too small
			
			double annPV = findPVAnnPV(i, n, pmt, due);
			double finalPV = findPV(i, n, fv);
			approx = annPV+finalPV;
			
		}while(Math.abs(pv-approx)>error_threshold);
		return n;//i should now be calculated
	}
	
	/**
	 * For now, bond FV is considered to be ALWAYS $1000 (it's all I need for class).  I can change this later if I want.
	 * @param pv
	 * @param i
	 * @param pmt
	 * @param due
	 * @return
	 */
	public static double findBondPmt(double pv, double i, double n, boolean due)
	{
		double fv = 1000;
		double approx = 0;
		double pmt = 1;//Initial guess is 100% interest
		double step = 1;//Initial alteration step is 1
		double error = Double.NaN;
		double prevError = Double.NaN;
		do{
			if( error == Double.NaN )
			{
				error = pv-approx;
			}else
			{
				prevError = error;
				error = pv-approx;
			}
			if( error == 0 )//If, by some miracle, the error is gone, exit the loop.  We found i.
				break;
			
			if( (error > 0 && prevError < 0) || (error < 0 && prevError > 0 ) )//If the sign changed, half the step (if we passed over the number, decrease step amount)
			{
				step/=2;
			}
			
			if( error > 0 )	//If the error is positive
				pmt+=step;	//adjust payment
			else			//If the error is negative
				pmt-=step;	//adjust payment
			
			if( pmt == 0 )
				pmt = 0.000000000001;//Can't divide by zero; don't let it; unfortunately, it also breaks if the number is too small
			
			double annPV = findPVAnnPV(i, n, pmt, due);
			double finalPV = findPV(i, n, fv);//I could move this outside the loop; I will not so that this is consistent with the others
			approx = annPV+finalPV;
			
		}while(Math.abs(pv-approx)>error_threshold);
		return pmt;//i should now be calculated
	}
}
