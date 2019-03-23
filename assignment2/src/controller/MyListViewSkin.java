package controller;

import com.sun.javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.ListView;

@SuppressWarnings("restriction")
public class MyListViewSkin<T> extends ListViewSkin<T> {
	
	public MyListViewSkin(ListView<T> listView) {
		super(listView);
	}
	
	public void refresh() {
		super.flow.recreateCells();
	}

	
}