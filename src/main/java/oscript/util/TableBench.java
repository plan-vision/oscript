/*=============================================================================
 *     Copyright Texas Instruments 2003. All Rights Reserved.
 *   
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * $ProjectHeader: OSCRIPT 0.155 Fri, 20 Dec 2002 18:34:22 -0800 rclark $
 */


package oscript.util;

import java.util.Hashtable;

/**
 * A benchmark to compare OpenHashSymbolTable to a java.util.Hashtable for
 * operations that are likely to be commonly used by the script engine
 * 
 * @author Rob Clark
 * @version 0.1
 */
public class TableBench
{
  private static int[]  TABLE_SIZES = { 1, 3, 5, 10, 30, 50 };
  private static int    INITIAL_SIZE = 3;
  private static float  LOAD  = 0.67f;
  private static int    COUNT = 1000000;
  private static Object VALUE = Boolean.TRUE;  // what the key maps to
  
  public static void main( String[] args )
    throws Throwable
  {
    for( int i=0; i<TABLE_SIZES.length; i++ )
    {
      long t;
      System.err.println("Results for table size: " + TABLE_SIZES[i]);
      
      System.gc(); Thread.sleep(200);
      
      //////////////////////////////////////////////////////////////////////////
      System.err.println("** TEST 1:  build and populate table");
      
      t = System.currentTimeMillis();
      for( int j=0; j<COUNT; j++ )
      {
        Hashtable tbl = new Hashtable( INITIAL_SIZE, LOAD );
        for( int k=0; k<=TABLE_SIZES[i]; k++ )
          tbl.put( Integer.valueOf(p(k)), VALUE );
      }
      System.err.println("   java.util.Hashtable:              " + (System.currentTimeMillis()-t) + "ms");
      System.gc(); Thread.sleep(200);
      
      t = System.currentTimeMillis();
      for( int j=0; j<COUNT; j++ )
      {
        SymbolMap tbl = new SymbolMap( new OpenHashSymbolTable( INITIAL_SIZE, LOAD ) );
        for( int k=0; k<=TABLE_SIZES[i]; k++ )
          tbl.put( p(k), VALUE );
      }
      System.err.println("   SymbolMap(OpenHashSymbolTable):   " + (System.currentTimeMillis()-t) + "ms");
      System.gc(); Thread.sleep(200);
      
      t = System.currentTimeMillis();
      for( int j=0; j<COUNT; j++ )
      {
        OpenHashSymbolTable tbl = new OpenHashSymbolTable( INITIAL_SIZE, LOAD );
        for( int k=0; k<=TABLE_SIZES[i]; k++ )
          tbl.create( p(k) );
      }
      System.err.println("   OpenHashSymbolTable:              " + (System.currentTimeMillis()-t) + "ms");
      System.gc(); Thread.sleep(200);
      
      //////////////////////////////////////////////////////////////////////////
      System.err.println("** TEST 2:  access member");
      
      {
        // first we need to populate the table:
        Hashtable tbl = new Hashtable( INITIAL_SIZE, LOAD );
        for( int k=0; k<TABLE_SIZES[i]; k++ )
          tbl.put( Integer.valueOf(p(k)), VALUE );
        
        // and then for the actual test:
        t = System.currentTimeMillis();
        for( int j=0; j<COUNT; j++ )
          tbl.get( Integer.valueOf( p(j % TABLE_SIZES[i]) ) );
        System.err.println("   java.util.Hashtable:              " + (System.currentTimeMillis()-t) + "ms");
      }
      System.gc(); Thread.sleep(200);
      
      {
        // first we need to populate the table:
        SymbolMap tbl = new SymbolMap( new OpenHashSymbolTable( INITIAL_SIZE, LOAD ) );
        for( int k=0; k<TABLE_SIZES[i]; k++ )
          tbl.put( p(k), VALUE );
        
        // and then for the actual test:
        t = System.currentTimeMillis();
        for( int j=0; j<COUNT; j++ )
          tbl.get( p(j % TABLE_SIZES[i]) );
        System.err.println("   SymbolMap(OpenHashSymbolTable):   " + (System.currentTimeMillis()-t) + "ms");
      }
      System.gc(); Thread.sleep(200);
      
      {
        // first we need to populate the table:
        OpenHashSymbolTable tbl = new OpenHashSymbolTable( INITIAL_SIZE, LOAD );
        for( int k=0; k<TABLE_SIZES[i]; k++ )
          tbl.create( p(k) );
        
        // and then for the actual test:
        t = System.currentTimeMillis();
        for( int j=0; j<COUNT; j++ )
          tbl.get( p(j % TABLE_SIZES[i]) );
        System.err.println("   OpenHashSymbolTable:              " + (System.currentTimeMillis()-t) + "ms");
      }
      System.gc(); Thread.sleep(200);
      
      //////////////////////////////////////////////////////////////////////////
      System.err.println("** TEST 3:  access non-member");
      
      {
        // first we need to populate the table:
        Hashtable tbl = new Hashtable( INITIAL_SIZE, LOAD );
        for( int k=0; k<TABLE_SIZES[i]; k++ )
          tbl.put( Integer.valueOf(p(k)), VALUE );
        
        // and then for the actual test:
        t = System.currentTimeMillis();
        for( int j=0; j<COUNT; j++ )
          tbl.get( Integer.valueOf( (j % TABLE_SIZES[i]) + TABLE_SIZES[i] + 1 ) );
        System.err.println("   java.util.Hashtable:              " + (System.currentTimeMillis()-t) + "ms");
      }
      System.gc(); Thread.sleep(200);
      
      {
        // first we need to populate the table:
        SymbolMap tbl = new SymbolMap( new OpenHashSymbolTable( INITIAL_SIZE, LOAD ) );
        for( int k=0; k<TABLE_SIZES[i]; k++ )
          tbl.put( p(k), VALUE );
        
        // and then for the actual test:
        t = System.currentTimeMillis();
        for( int j=0; j<COUNT; j++ )
          tbl.get( (j % TABLE_SIZES[i]) + TABLE_SIZES[i] + 1 );
        System.err.println("   SymbolMap(OpenHashSymbolTable):   " + (System.currentTimeMillis()-t) + "ms");
      }
      System.gc(); Thread.sleep(200);
      
      
      {
        // first we need to populate the table:
        OpenHashSymbolTable tbl = new OpenHashSymbolTable( INITIAL_SIZE, LOAD );
        for( int k=0; k<TABLE_SIZES[i]; k++ )
          tbl.create( p(k) );
        
        // and then for the actual test:
        t = System.currentTimeMillis();
        for( int j=0; j<COUNT; j++ )
          tbl.get( (j % TABLE_SIZES[i]) + TABLE_SIZES[i] + 1 );
        System.err.println("   OpenHashSymbolTable:              " + (System.currentTimeMillis()-t) + "ms");
      }
      System.gc(); Thread.sleep(200);
    }
  }
  
  private static final int p( int p )
  {
    return Primes.PRIMES[ p % Primes.PRIMES.length ];
  }
}



/*
 *   Local Variables:
 *   tab-width: 2
 *   indent-tabs-mode: nil
 *   mode: java
 *   c-indentation-style: java
 *   c-basic-offset: 2
 *   eval: (c-set-offset 'substatement-open '0)
 *   eval: (c-set-offset 'case-label '+)
 *   eval: (c-set-offset 'inclass '+)
 *   eval: (c-set-offset 'inline-open '0)
 *   End:
 */

