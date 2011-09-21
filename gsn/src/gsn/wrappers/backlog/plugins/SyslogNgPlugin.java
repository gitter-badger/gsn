package gsn.wrappers.backlog.plugins;

import java.io.Serializable;

import org.apache.log4j.Logger;

import gsn.beans.DataField;


/**
 * This plugin listens for incoming SyslogNg messages.
 * 
 * @author Tonio Gsell
 */
public class SyslogNgPlugin extends AbstractPlugin {
	
	private static DataField[] dataField = {
			new DataField("TIMESTAMP", "BIGINT"),
			new DataField("GENERATION_TIME", "BIGINT"),
			new DataField("DEVICE_ID", "INTEGER"),

			new DataField("LOG_MESSAGE", "BINARY")};

	private final transient Logger logger = Logger.getLogger( SyslogNgPlugin.class );

	@Override
	public byte getMessageType() {
		return gsn.wrappers.backlog.BackLogMessage.SYSLOG_NG_MESSAGE_TYPE;
	}

	@Override
	public DataField[] getOutputFormat() {
		return dataField;
	}


	@Override
	public String getPluginName() {
        return "SyslogNgPlugin";
	}

	@Override
	public boolean messageReceived(int deviceId, long timestamp, Serializable[] data) {
		try {
			if( dataProcessed(System.currentTimeMillis(), new Serializable[]{timestamp, toLong(data[0])*1000, deviceId, ((String)data[1]).getBytes("UTF-8")}) ) {
				ackMessage(timestamp, super.priority);
				return true;
			} else {
				logger.warn("The message with timestamp >" + timestamp + "< could not be stored in the database.");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}
}