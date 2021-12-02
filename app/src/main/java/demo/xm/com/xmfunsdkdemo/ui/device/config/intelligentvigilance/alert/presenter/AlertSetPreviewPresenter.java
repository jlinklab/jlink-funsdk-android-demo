package demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.presenter;

import com.lib.sdk.bean.smartanalyze.Points;
import com.xm.ui.widget.drawgeometry.listener.GeometryInterface;
import com.xm.ui.widget.drawgeometry.model.GeometryPoints;

import java.util.ArrayList;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.view.AlertSetPreViewInterface;

public class AlertSetPreviewPresenter implements AlertSetPreViewInterface {
	private static final int CONVERT_PARAMETER = 8192;
	private GeometryInterface mDrawGeometry;
	public AlertSetPreviewPresenter(GeometryInterface geometry) {
		this.mDrawGeometry = geometry;
	}
	@Override
	public List<Points> getConvertPoint(int width, int height) {
		// TODO Auto-generated method stub
		List<Points> list = getVertex();
		for (Points points : list) {
			points.setX((int) (points.getX() * CONVERT_PARAMETER / width ));
			points.setY((int) (points.getY() * CONVERT_PARAMETER / height));
		}
		return list;
	}

	public void setConvertPoint(List<Points> list,int width,int height) {
		GeometryPoints[] points = new GeometryPoints[list.size()];
		for(int i = 0 ; i < list.size();++i) {
			points[i] = new GeometryPoints(list.get(i).getX() * width / CONVERT_PARAMETER ,list.get(i).getY() * height / CONVERT_PARAMETER);
		}
		mDrawGeometry.setGeometryPoints(points);
	}

	private List<Points> getVertex() {
		List<Points> list = new ArrayList<>();
		if(null != mDrawGeometry) {
			for (GeometryPoints points : mDrawGeometry.getVertex()) {
				list.add(new Points(points.x, points.y));
			}
		}
		return list;
	}
}
