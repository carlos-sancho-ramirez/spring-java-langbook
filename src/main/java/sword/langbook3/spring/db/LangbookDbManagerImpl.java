package sword.langbook3.spring.db;

import sword.database.Database;
import sword.langbook3.android.db.LangbookDatabaseManager2;

public final class LangbookDbManagerImpl extends LangbookDatabaseManager2<ConceptId, LanguageId, AlphabetId, CharacterId, CharacterCompositionTypeId, SymbolArrayId, CorrelationId, CorrelationArrayId, AcceptationId, BunchId, BunchSetId, RuleId, AgentId, QuizId, SentenceId> {
    public LangbookDbManagerImpl(Database db) {
        super(db, new ConceptIdManager(), new LanguageIdManager(), new AlphabetIdManager(), new CharacterIdManager(), new CharacterCompositionTypeIdManager(), new SymbolArrayIdManager(), new CorrelationIdManager(), new CorrelationArrayIdManager(), new AcceptationIdManager(), new BunchIdManager(), new BunchSetIdManager(), new RuleIdManager(), new AgentIdManager(), new QuizIdManager(), new SentenceIdManager());
    }
}
