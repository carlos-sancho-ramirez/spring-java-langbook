package sword.langbook3.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sword.database.MemoryDatabase;
import sword.langbook3.android.db.ImmutableCorrelation;
import sword.langbook3.android.db.ImmutableCorrelationArray;
import sword.langbook3.android.models.LanguageCreationResult;
import sword.langbook3.spring.db.AlphabetId;
import sword.langbook3.spring.db.ConceptId;
import sword.langbook3.spring.db.LangbookDbManagerImpl;
import sword.langbook3.spring.db.LanguageId;

@SpringBootApplication
public class LangbookApplication {

	private static AlphabetId preferredAlphabet;
	private static LangbookDbManagerImpl dbManager;

	private static void setUpDatabaseAndPreferences() {
		dbManager = new LangbookDbManagerImpl(new MemoryDatabase());
		final LanguageCreationResult<LanguageId, AlphabetId> langResult = dbManager.addLanguage("es");
		final AlphabetId alphabet = langResult.mainAlphabet;
		preferredAlphabet = alphabet;

		addSimpleAcceptation(dbManager, langResult.language.getConceptId(), alphabet, "Español");
		addSimpleAcceptation(dbManager, alphabet.getConceptId(), alphabet, "románico");

		addSimpleAcceptation(dbManager, alphabet, "casa");
		addSimpleAcceptation(dbManager, alphabet, "castillo");
		addSimpleAcceptation(dbManager, alphabet, "barraca");
	}

	public static LangbookDbManagerImpl getDbManager() {
		if (dbManager == null) {
			setUpDatabaseAndPreferences();
		}

		return dbManager;
	}

	public static AlphabetId getPreferredAlphabet() {
		// TODO: This value should change according to the user preferences. We need to manage cookies first
		if (preferredAlphabet == null) {
			setUpDatabaseAndPreferences();
		}

		return preferredAlphabet;
	}

	private static void addSimpleAcceptation(LangbookDbManagerImpl dbManager, ConceptId concept, AlphabetId alphabet, String text) {
		final ImmutableCorrelation<AlphabetId> correlation = new ImmutableCorrelation.Builder<AlphabetId>()
				.put(alphabet, text)
				.build();

		final ImmutableCorrelationArray<AlphabetId> correlationArray = new ImmutableCorrelationArray.Builder<AlphabetId>()
				.append(correlation)
				.build();

		dbManager.addAcceptation(concept, correlationArray);
	}

	private static void addSimpleAcceptation(LangbookDbManagerImpl dbManager, AlphabetId alphabet, String text) {
		addSimpleAcceptation(dbManager, dbManager.getNextAvailableConceptId(), alphabet, text);
	}

	public static void main(String[] args) {
		SpringApplication.run(LangbookApplication.class, args);
	}

}
