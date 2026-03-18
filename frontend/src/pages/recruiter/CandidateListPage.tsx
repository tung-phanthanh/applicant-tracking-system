import { useState, useEffect } from "react";
import api from "@/lib/api";

interface Candidate {
  id: string;
  fullName: string;
  email: string;
  phone: string;
  currentCompany: string;
  source: string;
  location: string;
  experienceYears: number;
  stage: string;
}

export default function CandidateListPage() {
  const [candidates, setCandidates] = useState<Candidate[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");

  useEffect(() => {
    fetchCandidates();
  }, []);

  const fetchCandidates = async () => {
    try {
      const response = await api.get("/candidates");
      setCandidates(response.data);
    } catch (error) {
      console.error("Failed to fetch candidates:", error);
    } finally {
      setLoading(false);
    }
  };

  const filteredCandidates = candidates.filter(
    (c) =>
      c.fullName.toLowerCase().includes(search.toLowerCase()) ||
      c.email.toLowerCase().includes(search.toLowerCase())
  );

  if (loading) {
    return <div className="p-6">Loading...</div>;
  }

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-6">Candidate List</h1>
      <div className="mb-4">
        <input
          type="text"
          placeholder="Search by name or email..."
          className="w-full max-w-md px-4 py-2 border rounded-lg"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">
                Name
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">
                Email
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">
                Phone
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">
                Stage
              </th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {filteredCandidates.map((candidate) => (
              <tr key={candidate.id} className="hover:bg-gray-50">
                <td className="px-4 py-3">{candidate.fullName}</td>
                <td className="px-4 py-3">{candidate.email}</td>
                <td className="px-4 py-3">{candidate.phone}</td>
                <td className="px-4 py-3">{candidate.stage}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
