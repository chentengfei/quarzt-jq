package com.szewec.quartz.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.szewec.quartz.model.TaskInfo;
import com.szewec.quartz.service.TaskService;

/**
 * 任务管理
 */
@Controller
@RequestMapping("/qt/api/task/")
public class TaskManageController {
	
	@Autowired
	private TaskService taskService;

	/**
	 * 任务列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="list")
	public String list(){
		Map<String, Object> map = new HashMap<>();
		List<TaskInfo> infos = taskService.list();
		map.put("rows", infos);
		map.put("total", infos.size());
		return JSON.toJSONString(map);
	}
	
	/**
	 * 保存定时任务
	 * @param info
	 */
	@ResponseBody
	@RequestMapping(value="save", produces = "application/json; charset=UTF-8")
	public String save(TaskInfo info){
		try {
			if(info.getId() == 0) {
				taskService.addJob(info);
			}else{
				taskService.edit(info);
			}
		} catch (Exception e) {
			return e.getMessage();
		}
		return "成功";
	}
	
	/**
	 * 删除定时任务
	 * @param jobName
	 * @param jobGroup
	 */
	@ResponseBody
	@RequestMapping(value="delete/{jobName}/{jobGroup}", produces = "application/json; charset=UTF-8")
	public String delete(@PathVariable String jobName, @PathVariable String jobGroup){
		try {
			taskService.delete(jobName, jobGroup);
		} catch (Exception e) {
			return e.getMessage();
		}
		return "成功";
	}
	
	/**
	 * 暂停定时任务
	 * @param jobName
	 * @param jobGroup
	 */
	@ResponseBody
	@RequestMapping(value="pause/{jobName}/{jobGroup}", produces = "application/json; charset=UTF-8")
	public String pause(@PathVariable String jobName, @PathVariable String jobGroup){
		try {
			taskService.pause(jobName, jobGroup);
		} catch (Exception e) {
			return e.getMessage();
		}
		return "成功";
	}
	
	/**
	 * 重新开始定时任务
	 * @param jobName
	 * @param jobGroup
	 */
	@ResponseBody
	@RequestMapping(value="resume/{jobName}/{jobGroup}", produces = "application/json; charset=UTF-8")
	public String resume(@PathVariable String jobName, @PathVariable String jobGroup){
		try {
			taskService.resume(jobName, jobGroup);
		} catch (Exception e) {
			return e.getMessage();
		}
		return "成功";
	}
}