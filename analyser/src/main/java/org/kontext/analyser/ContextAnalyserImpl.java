package org.kontext.analyser;

import org.kontext.common.repositories.PropertiesRepository;
import org.kontext.common.repositories.PropertiesRepositoryImpl;
import org.kontext.data.DataSourceManager;

public class ContextAnalyserImpl implements ContextAnalyser {
	
	private static final PropertiesRepository propsRepo = PropertiesRepositoryImpl.getPropsRepo();
	private DataSourceManager dataSourceMgr;
	
	public ContextAnalyserImpl(DataSourceManager dataSourceMgr) {
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
	
	public DataSourceManager getDataSourceMgr() {
		return dataSourceMgr;
	}

}
