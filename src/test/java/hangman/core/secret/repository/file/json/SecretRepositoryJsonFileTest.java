package hangman.core.secret.repository.file.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import hangman.core.secret.Secret;
import hangman.core.secret.Secret.Category;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class SecretRepositoryJsonFileTest {
	
	@Test
	public void loadsFine() {
		SecretRepositoryJsonFile repo = SecretRepositoryJsonFile.loadFromFile("secrets-test.json");
		List<Secret> secrets = repo.findAllByCategory(Category.ANIMALS);
		
		assertNotNull(secrets);
		assertEquals(2, secrets.size());
		assertEquals(Secret.newSecret("Monkey", Category.ANIMALS), secrets.get(0));
		assertEquals(Secret.newSecret("Lion", Category.ANIMALS), secrets.get(1));
	}
	
	@Test
	public void loadSamePathsTest() {
		SecretRepositoryJsonFile.loadFromFile("secrets-test.json");
		SecretRepositoryJsonFile.loadFromFile("secrets-test.json");
		assertTrue("does it crash?", true); // checking if loading the same file twice causes exception
	}

	@Test(expected=SecretRepositoryAlreadyLoaded.class)
	public void loadDifferentPathsTest() {
		SecretRepositoryJsonFile.loadFromFile("secrets-test.json");
		SecretRepositoryJsonFile.loadFromFile("secrets-test-XXX.json");
	}
	
	@Test
	public void singletoneSimpleTest() {
		SecretRepositoryJsonFile repo1 = SecretRepositoryJsonFile.loadFromFile("secrets-test.json");
		SecretRepositoryJsonFile repo2 = SecretRepositoryJsonFile.loadFromFile("secrets-test.json");
		assertTrue("singletone?",repo1 == repo2); 
	}
	
	
	@Test
	public void singletoneConcurrencyTest() throws InterruptedException {
		final int threadsNo = 100;
		final Set<SecretRepositoryJsonFile> store = Collections.newSetFromMap(new ConcurrentHashMap<SecretRepositoryJsonFile, Boolean>());
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
//					System.out.println("ref: "+SecretRepositoryJsonFile.loadFromFile("secrets-test.json"));
					store.add(SecretRepositoryJsonFile.loadFromFile("secrets-test.json")); // add object to set
				}
			});
		}
			
		latch.countDown(); //release the latch
	
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.SECONDS);
		
		assertEquals("only one object", 1, store.size());
	}

}
