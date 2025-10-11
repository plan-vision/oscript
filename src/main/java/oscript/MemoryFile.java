package oscript;
public class MemoryFile
{
	public String content;
	public long lastModified = System.currentTimeMillis(); 
	public String name;
	public MemoryFile(String pathname,String content) 
	{
		this.name=pathname;
		this.content=content;
	}
	public String getName() {
        return name;
    }
	public String getContent() {
		return content;
	}	

	public long getLastModified() {
		return lastModified;
	}

	public boolean setLastModified( long lastModified ) {
		this.lastModified = lastModified;
		return true;
	}

	/**
	 * Change content of virtual script file
	 * @param content
	 */
	public void setContent( String content ) {
		this.content = content;
	}
	
	public String toString() {
		return this.content;
	}
}