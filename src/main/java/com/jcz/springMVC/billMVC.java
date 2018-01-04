package com.jcz.springMVC;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.jcz.bizImpl.SmbmsBillBizImpl;
import com.jcz.bizImpl.SmbmsProviderBizImpl;
import com.jcz.entity.SmbmsBill;
import com.jcz.entity.SmbmsBillExample;
import com.jcz.entity.SmbmsProvider;
import com.jcz.entity.SmbmsUser;

@Controller
public class billMVC {

	@Autowired
	@Qualifier("bBiz")
	private SmbmsBillBizImpl bBiz;
	
	@Autowired
	@Qualifier("pBiz")
	private SmbmsProviderBizImpl pBiz;
	
	
	public void setpBiz(SmbmsProviderBizImpl pBiz) {
		this.pBiz = pBiz;
	}

	public void setbBiz(SmbmsBillBizImpl bBiz) {
		this.bBiz = bBiz;
	}
	
	/*进入billlist页面   modify页面  view页面*/
	@RequestMapping("/bill.do")
	public String tobilllist(Model model,String method,String billid,String queryProductName,String queryProviderId,String queryIsPayment,String pageIndex){
		if("view".equals(method)){
			SmbmsBill bill=bBiz.selectByPrimaryKey(Long.parseLong(billid));
			
			bill.setProvidername(pBiz.selectByPrimaryKey(bill.getProviderid()).getProname());/*根据供应商id，获得供应商名字*/
			model.addAttribute("bill", bill);
			return "billview";
		}else if("modify".equals(method)){
			model.addAttribute("bill", bBiz.selectByPrimaryKey(Long.parseLong(billid)));
			return "billmodify";
		}else{			
			Map map=bBiz.selectByExample(null,queryProductName,queryProviderId,queryIsPayment,pageIndex);/*按照分页获取list 、和分页信息param*/
			Map map2=pBiz.selectByExample(null, null, null, null);/*获取供应商列表*/
			List<SmbmsBill> list2=new ArrayList<>();
			for (SmbmsBill bill : (List<SmbmsBill>)map.get("listuser")) {
				bill.setProvidername(pBiz.selectByPrimaryKey(bill.getProviderid()).getProname());/*根据供应商id，获得供应商名字*/
				list2.add(bill);
			}
			
			model.addAttribute("billList", list2);
			model.addAttribute("providerList", map2.get("listuser"));
			model.addAttribute("page", map.get("param"));
			model.addAttribute("queryProductName",queryProductName);
			model.addAttribute("queryProviderId", queryProviderId);
			model.addAttribute("queryIsPayment", queryIsPayment);
			return "billlist";
		}
	}
	
	/*删除bill页面   获取供应商列表*/
	@ResponseBody
	@RequestMapping("/delbill.do")
	public Object delbill(String billid,String method){
		Map map=new HashMap<>();
		if("delbill".equals(method)){
			if(bBiz.selectByPrimaryKey(Long.parseLong(billid))==null){
				map.put("delResult", "notexist");
			}
			int flag=bBiz.deleteByPrimaryKey(Long.parseLong(billid));
			if(flag>0){
				map.put("delResult", "true");
			}else{
				map.put("delResult", "false");
			}
		}else if("getproviderlist".equals(method)){
			Map map2=pBiz.selectByExample(null,null,null,null);
			map.put("list", map2.get("listuser"));
		}
		return JSONArray.toJSONString(map);
	}
	
	/*跳到添加bill页面*/
	@RequestMapping("/billadd.do")
	public String billadd(){
		
		return "billadd";
	}
	
	/*bill 各种save*/
	@RequestMapping("/billsave.do")
	public String billsave(String method,SmbmsBill bill,HttpSession session){
		if("add".equals(method)){
			SmbmsUser user=(SmbmsUser)session.getAttribute("userSession");
			bill.setCreatedby(user.getId());
			bill.setCreationdate(new Date());
			int flag=bBiz.insertSelective(bill);
			if(flag>0){
				return "redirect:/bill.do";
			}else{
				return "redirect:/billadd.do";
			}
		}else{
			SmbmsBillExample example=new SmbmsBillExample();
			SmbmsBillExample.Criteria criteria=example.createCriteria();
			int flag=bBiz.updateByExampleSelective(bill, example);
			if(flag>0){
				return "redirect:/bill.do";
			}else{
				return "redirect:/bill.do?method=modify&billid="+bill.getId();
			}
		}
	}
}
