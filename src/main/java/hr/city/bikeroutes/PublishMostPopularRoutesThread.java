package hr.city.bikeroutes;

import hr.city.publications.MostPopularRoutesPublisher;
import hr.city.publications.UserRoutesPublisher;

public class PublishMostPopularRoutesThread extends Thread {
	
	MostPopularRoutesPublisher mostPopularRoutesPublisher;
	
	public PublishMostPopularRoutesThread(MostPopularRoutesPublisher popularRoutes)
	{
		this.mostPopularRoutesPublisher = popularRoutes;
	}
	
	@Override
	public void run() {
		while (true) {
			mostPopularRoutesPublisher.publishMostPopularRoutes();
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