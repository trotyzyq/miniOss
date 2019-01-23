package com.trotyzyq.entity.bo;


import com.alibaba.fastjson.JSONObject;

/**
 * Json返回对象数据
 * @author whx
 *
 */
public class JsonObjectBO {

	private int code;  //业务状态（编码）
	private String message;  //反馈信息
	private JSONObject data;  //业务数据

    public JsonObjectBO() {
        super();
    }

	public JsonObjectBO(ResponseCode code, String message, JSONObject data) {
		this.code = code.getCode();
		this.message = message;
		this.data = data;
	}
	
	/**
	 * 业务状态（编码）
	 * @return the code
	 */
	public int getCode() {
		return code;
	}
	
	/**
	 * 业务状态（编码）
	 * @param code the code to set
	 */
	public void setCode(ResponseCode code) {
		this.code = code.getCode();
	}
	
	/**
	 * 反馈信息
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * 反馈信息
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * 业务数据
	 * @return the data
	 */
	public JSONObject getData() {
		return data;
	}
	
	/**
	 * 业务数据
	 * @param data the data to set
	 */
	public void setData(JSONObject data) {
		this.data = data;
	}

	/**
	 * 输出该对象的Json格式字符串
	 */
	@Override
	public String toString() {
		JSONObject json = new JSONObject();
		json.put("code", code);
		json.put("message", message);
		
		if(data != null){
			json.put("data", data);
		}else{
			json.put("data", "");
		}
		
		return json.toString();
	}
}
