select pubs.resource_id
from nextprot.publications pubs
inner join  nextprot.cv_publication_types pubtypes on ( pubs.cv_publication_type_id = pubtypes.cv_id)
left outer join nextprot.resource_properties rp on (pubs.resource_id = rp.resource_id and rp.property_name = 'database') 
where lower(pubs.title) like lower(:title)
limit 1
