/*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* Please send inquiries to huber AT ut DOT ee
*/

package edu.ut.mobile.network;

import java.lang.reflect.Method;
import java.util.Vector;

import hugo.weaving.DebugLog;

public class CloudController {
    private NetworkManagerClient NM = null;
    byte[] IPAddress = new byte[4];  // cloud address
    int port;                        // cloud port
    Object result = null;
    Object state = null;
    final Object waitob = new Object();
    Vector results = new Vector();

    public CloudController(){
        port = NetInfo.port;
        IPAddress[0] = NetInfo.IPAddress[0];
        IPAddress[1] = NetInfo.IPAddress[1];
        IPAddress[2] = NetInfo.IPAddress[2];
        IPAddress[3] = NetInfo.IPAddress[3];
    }

    @DebugLog
    public Vector execute(Method toExecute, Object[] paramValues, Object state, Class stateDataType) {
        synchronized (waitob){
            this.result = null;
            this.state = null;
            

            if(NM == null){
                NM = new NetworkManagerClient(IPAddress, port);
                NM.setNmf(this);
            }

            new Thread(new StartNetwork(toExecute, paramValues, state, stateDataType)).start();
            
            try {
                waitob.wait(NetInfo.waitTime);
            } catch (InterruptedException e) {
            }
            
            if(this.state != null){
                results.removeAllElements();
                results.add(this.result);
                results.add(this.state);
                return results;
            }else{
                return null;
            }
        }

    }

    @DebugLog
    public void setResult(Object result, Object cloudModel){
        synchronized (waitob){
            this.result = result;
            this.state = cloudModel;
            waitob.notify();
        }
    }

    class StartNetwork implements Runnable{
        Method toExecute;
        Class[] paramTypes;
        Object[] paramValues;
        Object state = null;
        Class stateDataType = null;

        @DebugLog
        public StartNetwork(Method toExecute, Object[] paramValues, Object state, Class stateDataType) {
            this.toExecute = toExecute;
            this.paramTypes = toExecute.getParameterTypes();
            this.paramValues = paramValues;
            this.state = state;
            this.stateDataType = stateDataType;
        }


        @DebugLog
        public void run() {
            boolean isconnected = NM.connect();
            if(isconnected){
                NM.send(toExecute.getName(), paramTypes, paramValues, state, stateDataType);
            }
        }

    }
}
