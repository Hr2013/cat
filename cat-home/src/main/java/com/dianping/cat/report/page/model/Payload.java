package com.dianping.cat.report.page.model;

import com.dianping.cat.report.ReportPage;
import com.dianping.cat.report.page.model.spi.ModelPeriod;
import com.site.web.mvc.ActionContext;
import com.site.web.mvc.ActionPayload;
import com.site.web.mvc.payload.annotation.FieldMeta;
import com.site.web.mvc.payload.annotation.PathMeta;

public class Payload implements ActionPayload<ReportPage, Action> {
	private ReportPage m_page;

	@FieldMeta("op")
	private Action m_action;

	// /<report>/<domain>/<period>
	@PathMeta("path")
	private String[] m_path;

	@FieldMeta("type")
	private String m_type;
	@FieldMeta("name")
	private String m_name;

	@Override
	public Action getAction() {
		return m_action;
	}

	public String getDomain() {
		if (m_path.length > 1) {
			return m_path[1];
		} else {
			return null;
		}
	}

	@Override
	public ReportPage getPage() {
		return m_page;
	}

	public ModelPeriod getPeriod() {
		if (m_path.length > 2) {
			return ModelPeriod.getByName(m_path[2], ModelPeriod.CURRENT);
		} else {
			return ModelPeriod.CURRENT;
		}
	}

	public String getReport() {
		if (m_path.length > 0) {
			return m_path[0];
		} else {
			return null;
		}
	}

	public void setAction(String action) {
		m_action = Action.getByName(action, Action.XML);
	}

	@Override
	public void setPage(String page) {
		m_page = ReportPage.getByName(page, ReportPage.MODEL);
	}

	public void setPath(String[] path) {
		m_path = path;
	}

	public String getType() {
   	return m_type;
   }

	public void setType(String type) {
   	m_type = type;
   }

	public String getName() {
   	return m_name;
   }

	public void setName(String name) {
   	m_name = name;
   }

	@Override
	public void validate(ActionContext<?> ctx) {
		if (m_action == null) {
			m_action = Action.XML;
		}
	}
}
