package hr.city.bikeroutes;

import hr.city.publications.UserRoutesPublisher;

public class PublishUserDataThread extends Thread{
	UserRoutesPublisher userRoutesPublisher;
	
	public PublishUserDataThread(UserRoutesPublisher userRoutes)
	{
		this.userRoutesPublisher= userRoutes;
	}
	
	@Override
	public void run() {
		while (true) {
			userRoutesPublisher.publishUserDataPublication();
			try {
				Thread.sleep(2 *   // minutes to sleep
			             	 60 *   // seconds to a minute
			             	 1000); // milliseconds to a second
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
