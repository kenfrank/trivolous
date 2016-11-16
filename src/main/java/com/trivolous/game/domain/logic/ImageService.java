package com.trivolous.game.domain.logic;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trivolous.game.dao.ImageDao;
import com.trivolous.game.domain.QuestionImage;

@Service
@Transactional
public class ImageService {
	@Autowired
	private ImageDao imageDao;
	protected final Log logger = LogFactory.getLog(getClass());
	// TODO (high) -- use files here instead of loading into memory! 
	// use imagemagick and call out to system to do conversion on files.  change should be isolated
	// to here, except maybe pass in local file instead of buffer. 
	private HashMap<Long, byte[]> imageMap = new HashMap<Long, byte[]>();

	// converts image and stores in a temporary cache using id (usually id is user id, so user can only store one.)
	// TODO -- this could cause some strange issues if same user is making questions in multiple sessions.  Assume this
	// wont happen for now.  could gaurd against this.  or redesign this.  but as images are stored in memory here
	// must be carefuL!
	// TODO -- consider client side option to resize!  http://www.plupload.com
	private void putTempImage(long id, byte[] image) 
	{
		// TODO (med) -- if image exists for this id, delete it... or does that happen just by reassigning it.
		InputStream in = new ByteArrayInputStream(image);
		BufferedImage bufferedImage = null;
		byte[] imageArray = null; 
		try {
			bufferedImage = ImageIO.read(in);
			logger.info("Image #"+ id + "uploaded ="+bufferedImage.toString());
			// Scale the image using the imgscalr library
			BufferedImage scaledImage = Scalr.resize(bufferedImage, 420);
			
		     //convert BufferedImage to byte array
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write( scaledImage, "jpg", baos );
			baos.flush();
			imageArray = baos.toByteArray();
			logger.debug("Scaled Image original = " + image.length + " scaled = " + imageArray.length);
			baos.close();
		} catch (IOException e) {
			// TODO (med) -- throw exception here instead of handling it.
			e.printStackTrace();
		}
		
		if (imageArray != null)
		{
			imageMap.put(id, imageArray);
		}
	}
	
	private byte[] getTempImage(long id) 
	{
		return imageMap.get(id);
	}
	
	private boolean hasTempImage(long id)
	{
		return imageMap.containsKey(id);
	}
	
	private void removeTempImage(long id)
	{
		imageMap.remove(id);
	}
	
	// saves to temp storage
	private long saveTempImage(long id)
	{
		byte[] data = getTempImage(id);
		if (data != null) {
			long dbId = saveImageToDb(data);
			logger.info("Image saved as id = " + dbId);
			imageMap.remove(id);
			return dbId;
		}
		else {
			logger.warn("Image not found in cache id = " + id);
		}
			
		
		return -1;
	}

	private long saveImageToDb(byte[] data)
	{
		QuestionImage i = new QuestionImage();
		i.setData(data);
		imageDao.create(i);
		logger.info("save image. size=" + data.length + " id=" + i.getId());
		return i.getId();
	}
	
	public byte[] getImageById(long id)
	{
		QuestionImage i = imageDao.findById(id);
		if (i != null)
			return i.getData();
		return null;
	}	

	// TODO -- add member to image record in db.	
	public long putImage(byte[] image) 
	{
		// TODO -- use files instead
		long tempId = 0;
		while (hasTempImage(tempId)) {
			tempId++;
		}
		putTempImage(tempId, image);
		long id = saveTempImage(tempId);
		return id;
	}
	
	public void removeImage(long id) {
		// TODO -- need to remove from database!!!
	}

}
