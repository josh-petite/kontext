package org.kontext.analyser;

import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.data.DataSourceManager;

public class ContextAnalyserImpl implements ContextAnalyser {
	
	private PropertiesRepository propsRepo;
	private DataSourceManager dataSourceMgr;
	
	public ContextAnalyserImpl(PropertiesRepository propsRepo, DataSourceManager dataSourceMgr) {
		this.propsRepo = propsRepo;
		this.dataSourceMgr = dataSourceMgr;
	}

	/*
	 * Works by batches (= create_date)
	 * 
	 * (non-Javadoc)
	 * @see org.kontext.analyser.ContextAnalyser#analyser()
	 */
	@Override
	public void analyse() {
		
	}
	
	public PropertiesRepository getPropertiesRepository() {
		return propsRepo;
	}
	
	public DataSourceManager getDataSourceMgr() {
		return dataSourceMgr;
	}

}
