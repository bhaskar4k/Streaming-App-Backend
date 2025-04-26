package com.app.authentication;

import com.app.authentication.bloomfilter.BloomFilter;
import com.app.authentication.common.DbWorker;
import com.app.authentication.repository.TMstUserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication(scanBasePackages = "com.app.authentication")
public class AuthenticationApplication {
	@Autowired
	private EntityManager entityManager;
	private final DbWorker dbWorker = new DbWorker();
	List<Object> params = new ArrayList<>();

	public static BloomFilter<String> BloomFilter;

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationApplication.class, args);
	}

	@PostConstruct
	public void initBloomFilter() {
		try {
			System.out.println("\n======================================================================");
			System.out.println("\nSeeding Bloom Filter\n");

			String sql_string = "select email from t_mst_user limit 1000000 offset 0";
			List<Object[]> results = dbWorker.getQuery(sql_string, entityManager, params, null).getResultList();

			BloomFilter = new BloomFilter<>(1000000, 0.01);

			for (Object row : results) {
				String emailId = (row != null) ? (String) row : "";
				if(emailId.isEmpty() || emailId == "") continue;

				BloomFilter.add(emailId);
			}

			System.out.println("\nDone Seeding Bloom Filter\n");
			System.out.println("======================================================================\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
