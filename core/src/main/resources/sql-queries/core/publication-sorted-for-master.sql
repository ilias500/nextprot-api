select p1.*, pubtypes.cv_name as pub_type, rp.property_value as submission_database,
  rp2.property_value as journal_from_property,
  (select rp2.property_value from nextprot.resource_properties rp2 where rp2.resource_id=p1.resource_id and rp2.property_name='publisher') as publisher,
  (select rp2.property_value from nextprot.resource_properties rp2 where rp2.resource_id=p1.resource_id and rp2.property_name='city') as city,
  (select rp2.property_value from nextprot.resource_properties rp2 where rp2.resource_id=p1.resource_id and rp2.property_name='country') as country,
  (select rp2.property_value from nextprot.resource_properties rp2 where rp2.resource_id=p1.resource_id and rp2.property_name='institute') as institute,
  (select rp2.property_value from nextprot.resource_properties rp2 where rp2.resource_id=p1.resource_id and rp2.property_name='comment') as title_for_web_resource
from nextprot.publications p1 
inner join nextprot.view_master_publication_assoc actpub on (actpub.pub_id = p1.resource_id and actpub.entry_id= :identifierId and actpub.pub_type_id in ( :publicationTypes) )
inner join nextprot.cv_publication_types as pubtypes on p1.cv_publication_type_id = pubtypes.cv_id 
left outer join nextprot.cv_journals as j on (p1.cv_journal_id = j.cv_id) 
left outer join nextprot.resource_properties rp on (p1.resource_id = rp.resource_id and rp.property_name = 'database')
left outer join nextprot.resource_properties rp2 on (p1.resource_id = rp2.resource_id and rp2.property_name = 'journal')
order by 
  extract (year from p1.publication_date) desc,  
  p1.cv_publication_type_id,  j.journal_name asc,  
  p1.volume asc,  
  p1.first_page asc
            