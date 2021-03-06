package org.nextprot.api.user.service;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.nextprot.api.commons.exception.SearchQueryException;
import org.nextprot.api.commons.utils.StringGenService;
import org.nextprot.api.security.service.JWTCodec;
import org.nextprot.api.user.dao.UserProteinListDao;
import org.nextprot.api.user.domain.UserProteinList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class UserProteinListServiceTest extends AbstractUnitBaseTest {

	// Check why the following annotation could be problematic !!!!!
	// http://tedvinke.wordpress.com/2014/02/13/mockito-why-you-should-not-use-injectmocks-annotation-to-autowire-fields/
	@InjectMocks
	@Autowired
	private UserProteinListService proteinListService;

	@Mock
	private UserProteinListDao dao;

	@Mock
	private JWTCodec<Map<String, String>> codec;

    @Mock
    private StringGenService stringGenerator;

	@Before
	public void init() {

		MockitoAnnotations.initMocks(this);
    }

	@Test
	public void testCreateProteinList() {

		final UserProteinList proteinList = createUserProteinList("awesome", Sets.newHashSet("NX_P123"));

		dressMockedUserProteinListDao(proteinList, 10);

		UserProteinList created = proteinListService.createUserProteinList(proteinList);

		assertEquals(10, created.getId());
		assertEquals("awesome", created.getName());
		assertEquals(Sets.newHashSet("NX_P123"), created.getAccessionNumbers());

        Mockito.verify(stringGenerator, times(1)).generateString();
	}

    @Test
    public void testGeneratePubIdInCreateProteinList() {

        UserProteinList proteinList = createUserProteinList("awesome", Sets.newHashSet("NX_P123"));

        Mockito.when(dao.createUserProteinList(proteinList))
                .thenThrow(new DuplicateKeyException("PreparedStatementCallback; SQL [INSERT INTO np_users.user_protein_lists (list_name, description, owner_id, public_id)\n" +
                        "VALUES (?, ?, ?, ?);]; ERROR: duplicate key value violates unique constraint \"user_protein_lists_pubid_udx\"\n" +
                        "  Detail: Key (public_id)=(00000001) already exists.; nested exception is org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"user_protein_lists_pubid_udx\"\n" +
                        "  Detail: Key (public_id)=(00000001) already exists."))
                .thenThrow(new DuplicateKeyException("PreparedStatementCallback; SQL [INSERT INTO np_users.user_protein_lists (list_name, description, owner_id, public_id)\n" +
                        "VALUES (?, ?, ?, ?);]; ERROR: duplicate key value violates unique constraint \"user_protein_lists_pubid_udx\"\n" +
                        "  Detail: Key (public_id)=(00000001) already exists.; nested exception is org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint \"user_protein_lists_pubid_udx\"\n" +
                        "  Detail: Key (public_id)=(00000001) already exists."))
                .thenReturn(1L);

        proteinListService.createUserProteinList(proteinList);

        Mockito.verify(stringGenerator, times(3)).generateString();
    }

    @Test(expected = DuplicateKeyException.class)
    public void testGeneratePubIdInCreateProteinList2() {

        UserProteinList proteinList = createUserProteinList("awesome", Sets.newHashSet("NX_P123"));

        Mockito.when(dao.createUserProteinList(proteinList))
                .thenThrow(new DuplicateKeyException("user_protein_lists_pubid_udx"))
                .thenThrow(new DuplicateKeyException("user_protein_lists_pubid_udx"))
                .thenThrow(new DuplicateKeyException("user_protein_lists_pubid_udx"))
                .thenThrow(new DuplicateKeyException("user_protein_lists_pubid_udx"))
                .thenThrow(new DuplicateKeyException("user_protein_lists_pubid_udx"))
                .thenThrow(new DuplicateKeyException("user_protein_lists_pubid_udx"))
                .thenThrow(new DuplicateKeyException("user_protein_lists_pubid_udx"))
                .thenThrow(new DuplicateKeyException("user_protein_lists_pubid_udx"))
                .thenThrow(new DuplicateKeyException("user_protein_lists_pubid_udx"))
                .thenThrow(new DuplicateKeyException("user_protein_lists_pubid_udx"))
                .thenThrow(new DuplicateKeyException("user_protein_lists_pubid_udx"))
                .thenThrow(new DuplicateKeyException("user_protein_lists_pubid_udx"))

                .thenReturn(1L);

        proteinListService.createUserProteinList(proteinList);

        Mockito.verify(stringGenerator, times(3)).generateString();
    }

    @Test(expected = DuplicateKeyException.class)
    public void testGeneratePubIdInCreateProteinList3() {

        UserProteinList proteinList = createUserProteinList("awesome", Sets.newHashSet("NX_P123"));

        Mockito.when(dao.createUserProteinList(proteinList))
                .thenThrow(new DuplicateKeyException(""))
                .thenReturn(1L);

        proteinListService.createUserProteinList(proteinList);
    }

	public void getProteinListSearchResult() throws SearchQueryException {
		//TODO rewrite this code on the solr module 
		/*Set<String> accs = new HashSet<String>();
		accs.add("NX_P01308");
		accs.add("NX_P06213");
		
		proteinList.setAccessions(accs);
		List<SearchResultItem> docs = this.solrService.getUserListSearchResult(proteinList).getResults();
		
		assertEquals(2, docs.size());
		assertEquals("NX_P01308", docs.get(0).getProperties().get("id"));
		assertEquals("NX_P06213", docs.get(1).getProperties().get("id"));*/
	}

	/*@Test
	public void testAddAccessions() {

		Set<String> s1 = new HashSet<String>();
		s1.add("NX_P123");
		s1.add("NX_P456");

		UserProteinList l1 = proteinListService.createUserProteinList(mockSpongeBobProteinList("cool1", s1));

		Set<String> accs = new HashSet<String>();
		s1.add("NX_P124");

		this.proteinListService.updateUserProteinListMetadata(l1.getId(), accs);
	}

	@Test
	public void testRemoveAccessions() {
		Set<String> s1 = new HashSet<String>();
		s1.add("NX_P123");
		s1.add("NX_P456");

		UserProteinList l1 = this.proteinListService.createUserProteinList(mockSpongeBobProteinList("cool1", s1));

		assertEquals("cool1", l1.getName());
		assertEquals(2, l1.getAccessionNumbers().size());

		Set<String> remAcc = new HashSet<String>();
		remAcc.add("NX_P123");
		this.proteinListService.removeAccessionNumbers(l1.getId(), remAcc);

		l1 = this.proteinListService.getUserProteinListById(l1.getId());
		assertEquals("cool1", l1.getName());
		assertEquals(1, l1.getAccessionNumbers().size());

	}*/

	public static UserProteinList createUserProteinList(String name, Set<String> accessions) {

		UserProteinList ul = new UserProteinList();

		ul.setName(name);
		ul.setAccessions(accessions);

		return ul;
	}

	private void dressMockedUserProteinListDao(final UserProteinList proteinList, final long serialId) {

		when(dao.createUserProteinList(proteinList)).thenReturn(serialId);
		/*when(dao.createUserProteinList(proteinList)).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {

				proteinList.setId(serialId);

				return serialId;
			}
		});*/
		when(dao.getUserProteinListById(serialId)).thenReturn(proteinList);
	}
}
