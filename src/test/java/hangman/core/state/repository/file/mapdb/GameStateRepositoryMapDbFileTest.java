package hangman.core.state.repository.file.mapdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class GameStateRepositoryMapDbFileTest {

	@Test
	public void singletoneSimpleTest() {
		GameStateRepositoryMapDbFile one = GameStateRepositoryMapDbFile.getInstance();
		GameStateRepositoryMapDbFile two = GameStateRepositoryMapDbFile.getInstance();
		assertTrue(one==two);
	}
	
	@Test
	public void singletoneConcurrencyTest() throws InterruptedException {
		final int threadsNo = 100;
		final Set<GameStateRepositoryMapDbFile> store = Collections.newSetFromMap(new ConcurrentHashMap<GameStateRepositoryMapDbFile, Boolean>());
		final CountDownLatch latch = new CountDownLatch(1);
		final ExecutorService executorService = Executors.newFixedThreadPool(threadsNo);
		for (int i = 0; i < threadsNo; i++) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					try {
//						System.out.println("await");
						latch.await(); // wait for the latch to be released
					} catch (InterruptedException e) {
						fail();
					}
//					System.out.println("ref: "+GameStateRepositoryMapDbFile.getInstance());
					store.add(GameStateRepositoryMapDbFile.getInstance()); // add object to set
				}
			});
		}
			
		latch.countDown(); //release the latch
	
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.SECONDS);
		
		assertEquals("only one object", 1, store.size());
	}

}
