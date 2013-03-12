/*//////////////////////////////////////////////////////////////////////
	This file is part of FTP, an client FTP.
	Copyright (C) 2013  Nicolas Barranger <wicowyn@gmail.com>

    FTP is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FTP is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FTP.  If not, see <http://www.gnu.org/licenses/>.
*///////////////////////////////////////////////////////////////////////

package FTP;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.portable.OutputStream;

public class TransferTask implements Runnable {
	private List<TransferTaskListener> listeners=new ArrayList<TransferTaskListener>();
	private BufferedInputStream in;
	private BufferedOutputStream out;
	
	public TransferTask(InputStream in, OutputStream out){
		this.in=new BufferedInputStream(in);
		this.out=new BufferedOutputStream(out);
	}
	
	@Override
	public void run() {
		byte[] buff=new byte[4096];
		int read;
		long totalRead=0;
		
		try {
			read=this.in.read(buff);

			while(read!=-1){
				totalRead+=read;
				notifyTransfered(totalRead);
				this.out.write(buff, 0, read);
				read=this.in.read(buff);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		notifyFinish();
	}

	public void addListener(TransferTaskListener listener){
		this.listeners.add(listener);
	}
	
	public boolean removeListener(TransferTaskListener listener){
		return this.listeners.remove(listener);
	}
	
	protected void notifyTransfered(long transfered){
		for(TransferTaskListener listener : this.listeners) listener.transfered(transfered);
	}
	
	protected void notifyFinish(){
		for(TransferTaskListener listener : this.listeners) listener.finish();
	}
}