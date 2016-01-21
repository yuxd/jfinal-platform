package com.platform.mvc.station;

import com.jfinal.aop.Before;
import com.platform.dto.ZtreeNode;
import com.platform.mvc.base.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 岗位管理
 * @author 董华健
 */
//@Controller(controllerKey = "/jf/platform/station")
public class StationController extends BaseController {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(StationController.class);
	
	private String pIds;
	private String names;
	private int orderIds;

	private String moduleIds;
	private String operatorIds;
	
	/**
	 * 岗位首页tree
	 */
	public void index() {
		render("/platform/station/tree.html");
	}

	/**
	 * 岗位treeData
	 */
	public void treeData()  {
		List<ZtreeNode> nodeList = StationService.service.childNodeData(getCxt(), ids);
		renderJson(nodeList);
	}

	/**
	 * 保存岗位
	 */
	@Before(StationValidator.class)
	public void save() {
		ids = StationService.service.save(pIds, names, orderIds);
		renderText(ids);
	}

	/**
	 * 准备更新岗位
	 */
	@Before(StationValidator.class)
	public void update() {
		StationService.service.update(ids, pIds, names);
		renderText(ids);
	}

	/**
	 * 删除岗位
	 */
	public void delete() {
		StationService.service.delete(ids);
		renderText(ids);
	}

	/**
	 * 获取岗位对应的功能
	 */
	public void getOperator(){
		Station station = Station.dao.findById(ids);
		renderJson(station);
	}

	/**
	 * 设置岗位对应的功能
	 */
	public void setOperator(){
		StationService.service.setOperator(ids, moduleIds, operatorIds);
		renderJson(ids);
	}
	
}


