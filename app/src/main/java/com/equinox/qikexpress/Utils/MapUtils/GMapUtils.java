package com.equinox.qikexpress.Utils.MapUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by mukht on 10/30/2016.
 */

public class GMapUtils {

    private Context context;
    private GoogleMap gMap;
    private int j;


    public void execute(Double sourceLat, final Double sourceLng, final Double destLat, final Double destLng, final Handler handleDistance) {
        final List<LatLng> path = new ArrayList<>();
        path.add(new LatLng(sourceLat, sourceLng));
        new AsyncTask<String, Void, List<LatLng>>() {
            @Override
            protected List<LatLng> doInBackground(String... params) {
                try {
                    final StringBuilder url = new StringBuilder("http://maps.googleapis.com/maps/api/directions/xml?language=eng&mode=driving");
                    url.append("&origin=");
                    url.append(params[0].replace(' ', '+'));
                    url.append("&destination=");
                    url.append(params[1].replace(' ', '+'));

                    final InputStream stream = new URL(url.toString()).openStream();
                    final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    documentBuilderFactory.setIgnoringComments(true);
                    final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    final Document document = documentBuilder.parse(stream);
                    document.getDocumentElement().normalize();

                    final String status = document.getElementsByTagName("status").item(0).getTextContent();
                    if (!"OK".equals(status)) {
                        return null;
                    }
                    Log.d("FETCHED DIRECTIONS", "Hurraaay.. Fetched the directions for place: " + params[0] + ", " + params[1]);
                    final Element elementLeg = (Element) document.getElementsByTagName("leg").item(0);
                    final NodeList nodeListStep = elementLeg.getElementsByTagName("step");
                    final int length = nodeListStep.getLength();
                    for (int i = 0; i < length; i++) {
                        final Node nodeStep = nodeListStep.item(i);
                        if (nodeStep.getNodeType() == Node.ELEMENT_NODE) {
                            final Element elementStep = (Element) nodeStep;
                            path.addAll(PolyUtil.decode(elementStep.getElementsByTagName("points").item(0).getTextContent()));
                            //decodePolylines(elementStep.getElementsByTagName("points").item(0).getTextContent());
                        }
                    }
                } catch (final Exception e) {
                    return null;
                }
                path.add(new LatLng(destLat, destLng));
                return path;
            }

            @Override
            protected void onPostExecute(List<LatLng> result) {
                super.onPostExecute(result);
                Double dist = 0.00, time = 0.00;
                LatLng temp = null;
                for (LatLng point : result) {
                    if (temp != null)
                        dist += SphericalUtil.computeDistanceBetween(temp, point);
                    temp = point;
                }
                time = ((dist/1000) / 20) * 60;
                Message message = new Message();
                message.arg1 = dist.intValue();
                message.arg2 = time.intValue();
                handleDistance.sendMessage(message);
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, sourceLat + ", " + sourceLng, destLat + ", " + destLng);
    }
}
