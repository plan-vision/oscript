/*=============================================================================
 *     Copyright Texas Instruments 2000.  All Rights Reserved.
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


package oscript;

import java.io.IOException;
import oscript.parser.OscriptParser;
import oscript.parser.ParseException;
import oscript.syntaxtree.Node;


/* XXX TODO: move this, and oscript.OscriptParser, etc. to oscript.parser
 * package.
 */

/**
 * 
 * @author Rob Clark (rob@ti.com)
 * <!--$Format: " * @version $Revision$"$-->
 * @version 1.3.1.4
 */
public class DefaultParser implements Parser
{
 
  /**
   * Get the file extension for file type to handle, eg. <code>os</code>.  This
   * is used to determine which parser to use for which file to parse.
   * 
   * @return the file extension
   */
  public String getExtension()
  {
    return "os";
  }

  // NOT SYNCHRONIZED ! LOCKING BY USER !

  /**
   * Convert a file to Node.
   * 
   * @param file       the file to parse
   * @return the parsed syntaxtree
   */
  public Node parse( MemoryFile file )
    throws ParseException, IOException
  {
      return parse(file.getContent());
  }
  
  public  static Node parse( String content) throws ParseException
  {
      OscriptParser.ReInit(content.split("\\n"));
      return OscriptParser.ProgramFile();
  }

  public  static Node parse( String lines[]) throws ParseException
  {
      OscriptParser.ReInit(lines);
      return OscriptParser.ProgramFile();
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


